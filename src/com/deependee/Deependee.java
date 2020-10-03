package com.deependee;

import com.deependee.parser.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Deependee {

    public static void main(String[] args) {

        Deependee dep = new Deependee();
        dep.interpret("x <- -2.0e3");
        dep.interpret("y <- [x, 3, y + 4, [2,3], { x : 2 }]");
        dep.interpret("f(x) <- x + \"text\"");
        dep.interpret("t <- u\nv <- w");
        dep.interpret("x <- { y : z }");
    }

    private void interpret(String source) {
        CodePointCharStream input = CharStreams.fromString(source);
        compile(input);
    }

    private void compile(CharStream source) {
        DeependeeLexer lexer = new DeependeeLexer(source);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        DeependeeParser parser = new DeependeeParser(tokenStream);
        Registry registry = new BasicRegistryImpl();
        DeependeeListener listener = new DeependeeListenerImpl(registry);
        parser.addParseListener(listener);
        parser.statements();
    }
}
