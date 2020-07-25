package com.deependee.parser;

public class DeependeeVisitorImpl extends DeependeeBaseVisitor {
    @Override
    public Object visitArray(DeependeeParser.ArrayContext ctx) {
        System.out.println(ctx.first.getText());
        System.out.println(ctx.next.getText());
        return super.visitArray(ctx);
    }

    @Override
    public Object visitFunction(DeependeeParser.FunctionContext ctx) {
        System.out.println(ctx.getText());
        return super.visitFunction(ctx);
    }

    @Override
    public Object visitDependency(DeependeeParser.DependencyContext ctx) {
        System.out.println(ctx.getText());
        return super.visitDependency(ctx);
    }

}
