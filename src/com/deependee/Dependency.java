package com.deependee;

import java.util.Optional;

public interface Dependency {
    Optional<Object> resolve();

    Optional<Object> trace();
}
