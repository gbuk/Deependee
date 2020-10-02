package com.deependee.parser;

public class Dependency {

    private ID id;
    private Function function;
    private Value value;
    private ExternalCall externalCall;

    public void setExternalCall(ExternalCall externalCall) {
        this.externalCall = externalCall;
    }

    public void setFunction(Function function) { this.function = function; }

    public void setId(ID id) {
        this.id = id;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public ExternalCall getExternalCall() {
        return externalCall;
    }

    public Function getFunction() {
        return function;
    }

    public ID getId() {
        return id;
    }

    public Value getValue() {
        return value;
    }
}
