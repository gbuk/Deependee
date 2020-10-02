package com.deependee.parser;

import com.deependee.Registry;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class DeependeeListenerImpl extends DeependeeBaseListener {

    Registry registry;

    Map<String, Object> elementsMap = new HashMap<>();
    Stack<Map<String,Object>> elementsStack = new Stack<>();

    public DeependeeListenerImpl(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void exitDependency(DeependeeParser.DependencyContext ctx) {

        if (elementsStack.empty())
            return;

        Map<String, Object> childrenMap = elementsStack.pop();

        Object leftCtx = ctx.children.get(0);
        Object rightCtx = ctx.children.get(2);

        Dependency dep = new Dependency();

        // dependency is a function definition
        if (leftCtx instanceof DeependeeParser.FunctionContext x_leftCtx) {
            dep.setFunction((Function)childrenMap.get(x_leftCtx.getText()));
        }

        // dependency is an ID
        if (leftCtx instanceof TerminalNode x_leftCtx) {
            dep.setId(new ID(x_leftCtx.getText()));
        }

        if (rightCtx instanceof DeependeeParser.External_callContext x_rightCtx) {
            dep.setExternalCall((ExternalCall)childrenMap.get(x_rightCtx.getText()));
        }

        if (rightCtx instanceof DeependeeParser.ValueContext x_rightCtx) {
            dep.setValue((Value)childrenMap.get(x_rightCtx.getText()));
        }

        elementsMap.put(ctx.getText(), dep);
    }

    @Override
    public void exitFunction(DeependeeParser.FunctionContext ctx) {

        Map<String, Object> childrenMap = elementsStack.pop();

        String name = ctx.getChild(0).getText();
        Value[] args = null;
        if (ctx.getChildCount() > 3) {
            args = new Value[(int) Math.ceil((ctx.getChildCount() - 3) / 2.0)];
            int j = 0;
            for(int i = 3; i<ctx.getChildCount()-2; i++) {
                args[j] = (Value)childrenMap.get(ctx.getChild(i).getText());
                j++;
            }
        }
        Function func = new Function(name, args);

        //store parsed result in parent
        elementsStack.peek().put(ctx.getText(), func);
    }

    @Override
    public void exitValue(DeependeeParser.ValueContext ctx) {
        Map<String, Object> childrenMap = elementsStack.pop();

        List<Object> components = new LinkedList<>();

        for(int i = 0; i < ctx.getChildCount(); i++) {
            System.out.println(ctx.getChild(i).getClass() + ": " + ctx.getChild(i).getText());
        }

        components.add(ctx.getText());
        Value val = new Value(components);

        //store parsed result in parent
        elementsStack.peek().put(ctx.getText(), val);
    }

    @Override
    public void enterValue(DeependeeParser.ValueContext ctx) {
        Map<String, Object> childrenMap = new HashMap<>();
        elementsStack.push(childrenMap);
    }

    @Override
    public void enterDependency(DeependeeParser.DependencyContext ctx) {
        Map<String, Object> childrenMap = new HashMap<>();
        elementsStack.push(childrenMap);
    }

    @Override
    public void enterFunction(DeependeeParser.FunctionContext ctx) {
        Map<String, Object> childrenMap = new HashMap<>();
        elementsStack.push(childrenMap);
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
