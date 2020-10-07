package com.deependee.parser;

import java.util.HashMap;
import java.util.Map;

public enum Operator {
    PLUS("+"), // addition, concatenation, boolean or
    TIMES("*"), // multiplication, boolean and
    DIVISION("/"), // division, string split
    MINUS("-"), // substraction, remove element
    NOT("!"), // not, inverse
    MODULO("%"), // modulo
    POWER("^"), // power
    QUESTION("??"), // query, index, find, search
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    EQUAL("="),
    GREATER_THAN_OR_EQUAL("=>"),
    GREATER_THAN(">"),
    LEFT_TERNARY_OPERATOR("?"),
    RIGHT_TERNARY_OPERATOR(":"),
    ;

    private String text;

    Operator(String text) {
        this.text = text;
    }

    private static final Map<String, Operator> textMap = new HashMap<>();
    static {
        for (Operator op : Operator.values()) {
            textMap.put(op.text, op);
        }
    }

    public static Operator mapToEnum(String operatorAsString) {
        return textMap.get(operatorAsString);
    }

}
