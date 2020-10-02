package com.deependee;

import java.util.Optional;

public class DeferredDependency implements Dependency {
    @Override
    public Optional<Object> resolve() {
        return Optional.empty();
    }

    @Override
    public Optional<Object> trace() {
        return Optional.empty();
    }
}
