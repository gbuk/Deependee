package com.deependee;

import com.deependee.generated.antlr.parser.DeependeeLexer;
import com.deependee.generated.antlr.parser.DeependeeParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.deependee.parser.DeependeeListenerImpl;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Deependee {

    private DeependeeListenerImpl listener = new DeependeeListenerImpl();

    private static String LOAD = ":load ";
    private static String TRACE = ":trace ";
    private static String CHECK = ":check ";
    private static String HELP = ":?";
    private static String EXIT = ":exit";

    public static void main(String[] args) {

        Deependee dep = new Deependee();
        dep.interpret("x <- -2.0e3");
        dep.interpret("y <- [x, 3, y + 4, [2,3], { x : 2 }]");
        dep.interpret("f(x) <- x + \"text\"");
        dep.interpret("t <- u\nv <- w");
        dep.interpret("x <- { y : z }");
        dep.interpret("x <- (( y + a) * z ) + t)");

        Reader inreader = new InputStreamReader(System.in);
        try {
            BufferedReader in = new BufferedReader(inreader);
            String str;
            while ((str = in.readLine()) != null) {
                String input = str.trim();
                if (input.equals(EXIT))
                    break;
                if (input.startsWith(LOAD)) {
                    String fileName = input.substring(LOAD.length()).trim();
                    dep.interpret(Files.readString(Paths.get(fileName)));
                    continue;
                }
                if (input.startsWith(TRACE)) {
                    String dependency = input.substring(TRACE.length()).trim();
                    System.out.println(dep.listener.trace(dependency));
                    continue;
                }
                if (input.startsWith(CHECK)) {
                    String dependency = input.substring(CHECK.length()).trim();
                    System.out.println(dep.listener.check(dependency));
                    continue;
                }
                if (input.equals(HELP)) {
                    System.out.println(":load <file path>");
                    System.out.println("   -> load dependencies definition file");
                    System.out.println(":trace <dependency>");
                    System.out.println("   -> trace dependency tree for the given dependency");
                    System.out.println(":check <dependency>");
                    System.out.println("   -> check constraints on the given dependency");
                    System.out.println(":?");
                    System.out.println("   -> this help output");
                    System.out.println(":exit");
                    System.out.println("   -> leave the REPL");
                    continue;
                }
                System.out.println(dep.interpret(str));
            }
            in.close();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public List<Object> interpret(String source) {
        CodePointCharStream input = CharStreams.fromString(source);
        DeependeeLexer lexer = new DeependeeLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        DeependeeParser parser = new DeependeeParser(tokenStream);
        parser.addParseListener(listener);
        parser.statements();
        return listener.getParsingResult();
    }
}
