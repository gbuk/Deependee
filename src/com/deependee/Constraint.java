package com.deependee;

public interface Constraint {
    enum Result {
        CONSTRAINT_IS_VALID,
        CONSTRAINT_IS_INVALID
    }
    Result check();
}
