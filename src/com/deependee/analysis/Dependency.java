package com.deependee.analysis;

import java.util.ArrayList;

public class Dependency {
    public ArrayList<Dependency> parents;
    public String value;

    public void printConsole(String indent) {
        if (indent == null) {
            System.out.println(value);
            indent = "  ";
        } else
            System.out.println(indent + '>' + value);
        for(Dependency parent : parents) {
            parent.printConsole(indent + indent);
        }
    }
}
