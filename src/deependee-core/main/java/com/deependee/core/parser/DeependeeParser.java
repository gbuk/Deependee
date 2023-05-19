package com.deependee.core.parser;

import com.deependee.generated.antlr.parser.DeependeeLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

public class DeependeeParser {

    DeependeeListenerImpl listener = new DeependeeListenerImpl();

    public List<Object> parse(String source) {
        CodePointCharStream input = CharStreams.fromString(source);
        DeependeeLexer lexer = new DeependeeLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        com.deependee.generated.antlr.parser.DeependeeParser parser = new com.deependee.generated.antlr.parser.DeependeeParser(tokenStream);
        parser.addParseListener(listener);
        parser.statements();
        return listener.getParsingResult();
    }
}
