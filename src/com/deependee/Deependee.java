package com.deependee;

import com.deependee.parser.DeependeeLexer;
import com.deependee.parser.DeependeeParser;
import com.deependee.parser.DeependeeVisitorImpl;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Deependee {

    public static void main(String[] args) {

        Deependee dep = new Deependee();
        dep.interpret("y <- [x, 3, 4, [2,3]]");
        dep.interpret("f(x) <- x + 2");
    }

    private void interpret(String source) {
        CodePointCharStream input = CharStreams.fromString(source);
        compile(input);
    }

    private Object compile(CharStream source) {
        DeependeeLexer lexer = new DeependeeLexer(source);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        DeependeeParser parser = new DeependeeParser(tokenStream);
        ParseTree tree = parser.expression();
        DeependeeVisitorImpl visitor = new DeependeeVisitorImpl();
        return visitor.visit(tree);
    }
}
