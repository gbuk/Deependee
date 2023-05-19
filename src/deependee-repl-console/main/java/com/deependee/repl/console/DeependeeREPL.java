package com.deependee;

import com.deependee.parser.DeependeeParser;

import com.deependee.parser.DeependeeListenerImpl;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DeependeeREPL {


    private static final String LOAD = ":load ";
    private static final String TRACE = ":trace ";
    private static final String CHECK = ":check ";
    private static final String HELP = ":?";
    private static final String EXIT = ":exit";

    public static void main(String[] args) {

        DeependeeParser dep = new DeependeeParser();
        DeependeeListenerImpl listener = new DeependeeListenerImpl();
        //DeependeeRegistry registry = new DeependeeRegistry();


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
                    try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
                        stream.forEach(line ->
                            System.out.println(dep.parse(line))
                        );
                    }
                    continue;
                }
                if (input.startsWith(TRACE)) {
                    String dependency = input.substring(TRACE.length()).trim();
                    System.out.println(listener.trace(dependency));
                    continue;
                }
                if (input.startsWith(CHECK)) {
                    String dependency = input.substring(CHECK.length()).trim();
                    System.out.println(listener.check(dependency));
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
                System.out.println(dep.parse(str));
            }
            in.close();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

}
