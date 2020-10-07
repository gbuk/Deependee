package com.deependee.parser;

import com.deependee.generated.antlr.parser.DeependeeBaseListener;
import com.deependee.generated.antlr.parser.DeependeeParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class DeependeeListenerImpl extends DeependeeBaseListener {

    Stack<Map<String,Object>> elementsStack = new Stack<>();

    List<Object> parsingResult = new ArrayList<>();
    Map<String, Dependency> dependencies = new HashMap<>();
    Map<String, List<Constraint>> constraints = new HashMap<>();

    @Override
    public void exitDependency(DeependeeParser.DependencyContext ctx) {

        exit(ctx, parsingMap -> {
            Object leftCtx = ctx.getChild(0);
            Object rightCtx = ctx.getChild(2);

            Function fun = null;
            ID id = null;
            Value val = null;
            ExternalCall call = null;

            // dependency is a function definition
            if (leftCtx instanceof DeependeeParser.FunctionContext x_leftCtx) {
                fun = (Function) parsingMap.get(x_leftCtx.getText());
            }

            // dependency is an ID
            if (leftCtx instanceof TerminalNode x_leftCtx) {
                id = new ID(x_leftCtx.getText());
            }

            if (rightCtx instanceof DeependeeParser.External_callContext x_rightCtx) {
                call = (ExternalCall) parsingMap.get(x_rightCtx.getText());
            }

            if (rightCtx instanceof DeependeeParser.ValueContext x_rightCtx) {
                val = (Value) parsingMap.get(x_rightCtx.getText());
            }

            return new Dependency(id, fun, val, call);
        });
    }

    @Override
    public void exitFunction(DeependeeParser.FunctionContext ctx) {
        exit(ctx, parsingMap -> {
            String name = ctx.getChild(0).getText();
            Value[] args;
            if (ctx.getChildCount() > 3) {
                args = new Value[(int) Math.ceil((ctx.getChildCount() - 3) / 2.0)];
                int j = 0;
                for (int i = 2; i < ctx.getChildCount() - 1; i++) {
                    if (",".equals(ctx.getChild(i).getText())) {
                        continue;
                    }
                    args[j] = (Value) parsingMap.get(ctx.getChild(i).getText());
                    j++;
                }
            } else {
                args = new Value[0];
            }
            return new Function(name, args);
        });
    }

    @Override
    public void exitNumber(DeependeeParser.NumberContext ctx) {
        exit(ctx, parsingMap -> {
            String valueString = ctx.getChild(0).getText();
            if (valueString.contains(".")) {
                return new Value(new BigDecimal(valueString));
            } else {
                return new Value(new BigInteger(valueString));
            }
        });
    }

    @Override
    public void exitValue(DeependeeParser.ValueContext ctx) {
        exit(ctx, parsingMap -> {
            // tertiary expression
            if (ctx.getChildCount() >= 5) {
                Operator operator = Operator.mapToEnum(read(ctx.getChild(1)));
                if (Operator.LEFT_TERNARY_OPERATOR.equals(operator)) {
                    Value leftOperand = mapValue(ctx.getChild(0), parsingMap);
                    Value middleOperand = mapValue(ctx.getChild(0), parsingMap);
                    Operator secondOperator = Operator.mapToEnum(read(ctx.getChild(1)));
                    Value rightOperand = mapValue(ctx.getChild(2), parsingMap);
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
                    Value leftOperand = mapValue(ctx.getChild(0), parsingMap);
                    Value rightOperand = mapValue(ctx.getChild(2), parsingMap);
                    return new Value(new BinaryExpression(leftOperand, operator, rightOperand));
                }
            }
            // unary expression
            if (ctx.getChildCount() >= 2) {
                Operator operator = Operator.mapToEnum(read(ctx.getChild(0)));
                if (operator != null) {
                    Value operand = mapValue(ctx.getChild(1), parsingMap);
                    return new Value(new UnaryExpression(operator, operand));
                }
            }

            // the expression is between brackets
            if ("(".equals(ctx.getChild(0).getText())) {
                return(mapValue(ctx.getChild(1), parsingMap));
            }
            // value, child count is 1 or any other case
            return mapValue(ctx.getChild(0), parsingMap);
        });
    }

    private Value mapValue(ParseTree ctx, Map<String, Object> parsingMap) {
        if (ctx instanceof TerminalNodeImpl) {
            String valueString = read(ctx);
            Object value;
            if (valueString.startsWith("\"")) {
                value = parseString(valueString);
            } else {
                value = new ID(valueString);
            }
            return new Value(value);
        }
        if (ctx instanceof DeependeeParser.ValueContext) {
            return (Value)parsingMap.get(read(ctx));
        }
        if (parsingMap.get(read(ctx)) instanceof Value value) {
            return value;
        }
        return new Value(parsingMap.get(read(ctx)));
    }

    @Override
    public void exitArray(DeependeeParser.ArrayContext ctx) {
        exit(ctx, parsingMap -> new Array(
            ctx.children.stream()
                .filter(c -> c instanceof DeependeeParser.ValueContext)
                .map(c -> (Value) parsingMap.get(read(c)))
                .toArray(Value[]::new)
            )
        );
    }

    @Override
    public void exitConstraint(DeependeeParser.ConstraintContext ctx) {
        exit(ctx, parsingMap -> {
            ID id = null;
            Function func = null;
            if (ctx.getChild(0) instanceof DeependeeParser.FunctionContext) {
                func = (Function)parsingMap.get(ctx.getChild(0).getText());
            } else {
                id = new ID(ctx.getChild(0).getText());
            }
            Operator operator = Operator.mapToEnum(read(ctx.getChild(2)));
            Value operand = mapValue(ctx.getChild(3), parsingMap);
            String rationale = (String)parsingMap.get(read(ctx.getChild(4)));
            return new Constraint(id, func, operator, operand, rationale);
        });
    }

    @Override
    public void exitExternal_call(DeependeeParser.External_callContext ctx) {
        exit(ctx, parsingMap -> new ExternalCall(new ID(ctx.getChild(0).getText()), ctx.getChild(2).getText()));
    }

    @Override
    public void exitObject(DeependeeParser.ObjectContext ctx) {
        exit(ctx, parsingMap -> new Obj(
            ctx.children.stream()
                .map(c -> (Pair) parsingMap.get(read(c)))
                .toArray(Pair[]::new))
        );
    }

    @Override
    public void exitPair(DeependeeParser.PairContext ctx) {
        exit(ctx, parsingMap -> new Pair(
            new ID(ctx.getChild(0).getText()),
            (Value)parsingMap.get(ctx.getChild(2).getText())
        ));
    }

    @Override
    public void exitStatement(DeependeeParser.StatementContext ctx) {
        exit(ctx, parsingMap -> {
            boolean isDependency = ctx.getChild(0) instanceof DeependeeParser.DependencyContext;
            boolean isConstraint = ctx.getChild(0) instanceof DeependeeParser.ConstraintContext;
            if (isDependency || isConstraint) {
                Object obj = parsingMap.get(ctx.getChild(0).getText());
                if (isDependency) {
                    Dependency dep = (Dependency)obj;
                    if (dep.id() != null) {
                        dependencies.put(dep.id().toString(), dep);
                    }
                    if (dep.function() != null) {
                        dependencies.put(dep.function().name(), dep);
                    }
                }
                if (isConstraint) {
                    assert obj instanceof Constraint; // not necessary but IntelliJ complains otherwise
                    Constraint constraint = (Constraint)obj;
                    String key;
                    if (constraint.id() != null) {
                        key = constraint.id().text();
                    } else {
                        key = constraint.function().name();
                    }
                    List<Constraint> constraintsList = constraints.computeIfAbsent(key, k -> new ArrayList<>());
                    constraintsList.add(constraint);
                }
                parsingResult.add(obj);
            }
            return null;
        });
    }

    @Override
    public void exitStatements(DeependeeParser.StatementsContext ctx) {
        exit(ctx, parsingMap -> null);
    }

    @Override
    public void exitRationale(DeependeeParser.RationaleContext ctx) {
        exit(ctx, parsingMap -> parseString(ctx.getChild(1).getText()));
    }

    public String trace(String dependency) {
        return dependencies.get(dependency).toString();
    }

    public String check(String dependency) {
        return constraints.get(dependency).toString();
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        enter();
    }

    private String parseString(String tokenString) {
        // TODO: parse special characters
        // TODO: parse unicode
        // remove quotes around the string
        return tokenString.substring(1, tokenString.length()-1);
    }

    private void enter() {
        Map<String, Object> parsingMap = new HashMap<>();
        elementsStack.push(parsingMap);
    }

    interface ExitLambda {
        Object getObject(Map<String, Object> parsingMap);
    }

    public void exit(Object ctx, ExitLambda exit) {
        if (elementsStack.empty()) {
            // this case should never happen
            // but we are guarding against exception on pop()
            return;
        }
        Map<String, Object> parsingMap = elementsStack.pop();
        // copy the elements from the parents
        elementsStack.forEach(parsingMap::putAll);
        Object parseResult = exit.getObject(parsingMap);
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

    public List<Object> getParsingResult() {
        return parsingResult;
    }

}
