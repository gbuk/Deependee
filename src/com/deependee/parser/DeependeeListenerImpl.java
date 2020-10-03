package com.deependee.parser;

import com.deependee.Registry;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class DeependeeListenerImpl extends DeependeeBaseListener {

    Registry registry;

    Map<String, Object> elementsMap = new HashMap<>();
    Stack<Map<String,Object>> elementsStack = new Stack<>();

    public DeependeeListenerImpl(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void exitDependency(DeependeeParser.DependencyContext ctx) {

        exit(ctx, childrenMap -> {
            //System.out.println(read(ctx.children.get(0)) + " " + read(ctx.children.get(1)) + " " + read(ctx.children.get(2)));
            Object leftCtx = ctx.getChild(0);
            Object rightCtx = ctx.getChild(2);

            Function fun = null;
            ID id = null;
            Value val = null;
            ExternalCall call = null;

            // dependency is a function definition
            if (leftCtx instanceof DeependeeParser.FunctionContext x_leftCtx) {
                fun = (Function) childrenMap.get(x_leftCtx.getText());
            }

            // dependency is an ID
            if (leftCtx instanceof TerminalNode x_leftCtx) {
                id = new ID(x_leftCtx.getText());
            }

            if (rightCtx instanceof DeependeeParser.External_callContext x_rightCtx) {
                call = (ExternalCall) childrenMap.get(x_rightCtx.getText());
            }

            if (rightCtx instanceof DeependeeParser.ValueContext x_rightCtx) {
                val = (Value) childrenMap.get(x_rightCtx.getText());
            }

            return new Dependency(id, fun, val, call);
        });
    }

    @Override
    public void exitFunction(DeependeeParser.FunctionContext ctx) {
        exit(ctx, childrenMap -> {
            String name = ctx.getChild(0).getText();
            Value[] args = null;
            if (ctx.getChildCount() > 3) {
                args = new Value[(int) Math.ceil((ctx.getChildCount() - 3) / 2.0)];
                int j = 0;
                for (int i = 3; i < ctx.getChildCount() - 2; i++) {
                    args[j] = (Value) childrenMap.get(ctx.getChild(i).getText());
                    j++;
                }
            }
            return new Function(name, args);
        });
    }

    @Override
    public void exitNumber(DeependeeParser.NumberContext ctx) {
        exit(ctx, childrenMap -> {
            String valueString = ctx.getChild(0).getText();
            Object value;
            if (valueString.contains(".")) {
                value = new BigDecimal(valueString);
            } else {
                value = new BigInteger(valueString);
            }
            return new Value(value);
        });
    }

    @Override
    public void exitValue(DeependeeParser.ValueContext ctx) {
        exit(ctx, childrenMap -> {
            // tertiary expression
            if (ctx.getChildCount() >= 5) {
                Operator operator = Operator.mapToEnum(read(ctx.getChild(1)));
                if (Operator.LEFT_TERNARY_OPERATOR.equals(operator)) {
                    Value leftOperand = mapValue(ctx.getChild(0), childrenMap);
                    Value middleOperand = mapValue(ctx.getChild(0), childrenMap);
                    Operator secondOperator = Operator.mapToEnum(read(ctx.getChild(1)));
                    Value rightOperand = mapValue(ctx.getChild(2), childrenMap);
                    return new Value(new TernaryExpression(
                        leftOperand,
                        operator,
                        middleOperand,
                        secondOperator,
                        rightOperand
                    ));
                }
            }
            // binary expression
            if (ctx.getChildCount() >= 3) {
                Operator operator = Operator.mapToEnum(read(ctx.getChild(1)));
                if (operator != null) {
                    Value leftOperand = mapValue(ctx.getChild(0), childrenMap);
                    Value rightOperand = mapValue(ctx.getChild(2), childrenMap);
                    return new Value(new BinaryExpression(leftOperand, operator, rightOperand));
                }
            }
            // unary expression
            if (ctx.getChildCount() >= 2) {
                Operator operator = Operator.mapToEnum(read(ctx.getChild(0)));
                if (operator != null) {
                    Value operand = mapValue(ctx.getChild(1), childrenMap);
                    return new Value(new UnaryExpression(operator, operand));
                }
            }

            // value, child count is 1 or any other case
            return mapValue(ctx.getChild(0), childrenMap);
        });
    }

    private Value mapValue(ParseTree ctx, Map<String, Object> childrenMap) {
        if (ctx instanceof TerminalNodeImpl) {
            String valueString = read(ctx);
            Object value;
            if (valueString.startsWith("\"")) {
                value = valueString.substring(1, valueString.length()-2);
            } else {
                value = new ID(valueString);
            }
            return new Value(value);
        }
        if (ctx instanceof DeependeeParser.ValueContext) {
            return (Value)childrenMap.get(read(ctx));
        }
        return new Value(childrenMap.get(read(ctx)));
    }

    @Override
    public void exitArray(DeependeeParser.ArrayContext ctx) {
        exit(ctx, childrenMap -> new Array(
            ctx.children.stream()
                .filter(c -> c instanceof DeependeeParser.ValueContext)
                .map(c -> (Value) childrenMap.get(read(c)))
                .toArray(Value[]::new)
            )
        );
    }

    @Override
    public void exitConstraint(DeependeeParser.ConstraintContext ctx) {
        exit(ctx, childrenMap -> {
            Operator operator = Operator.mapToEnum(read(ctx.getChild(0)));
            Value operand = mapValue(ctx.getChild(1), childrenMap);
            return new Constraint(operator, operand);
        });
    }

    @Override
    public void exitExternal_call(DeependeeParser.External_callContext ctx) {
        exit(ctx, childrenMap -> new ExternalCall(new ID(ctx.getChild(0).getText()), ctx.getChild(2).getText()));
    }

    @Override
    public void exitObject(DeependeeParser.ObjectContext ctx) {
        exit(ctx, childrenMap -> new Obj(
            ctx.children.stream()
                .map(c -> (Pair) childrenMap.get(read(c)))
                .toArray(Pair[]::new))
        );
    }

    @Override
    public void exitPair(DeependeeParser.PairContext ctx) {
        exit(ctx, childrenMap -> new Pair(
            new ID(ctx.getChild(0).getText()),
            (Value)childrenMap.get(ctx.getChild(2).getText())
        ));
    }

    @Override
    public void exitStatement(DeependeeParser.StatementContext ctx) {
        exit(ctx, childrenMap -> null);
    }

    @Override
    public void exitStatements(DeependeeParser.StatementsContext ctx) {
        exit(ctx, childrenMap -> null);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        enter();
    }

    private void enter() {
        Map<String, Object> childrenMap = new HashMap<>();
        elementsStack.push(childrenMap);
    }

    interface ExitLambda {
        Object getObject(Map<String, Object> childrenMap);
    }

    public void exit(Object ctx, ExitLambda exit) {
        if (elementsStack.empty())
            return;
        Map<String, Object> childrenMap = elementsStack.pop();
        Object parseResult = exit.getObject(childrenMap);
        //TODO: find out what to do with the parse result if it's the top element of the hierarchy
        if (!elementsStack.empty()) {
            elementsStack.peek().put(read(ctx), parseResult);
        }
    }

    private String read(Object ctx) {
        if (Optional.ofNullable(ctx).isEmpty()) {
            return "null";
        }
        try {
            return (String)ctx.getClass().getMethod("getText").invoke(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }
}
