package com.deependee;

import java.util.Optional;

public class DeferredValue implements Value {
    @Override
    public Optional<Object> get() {
        return Optional.empty();
    }
}
