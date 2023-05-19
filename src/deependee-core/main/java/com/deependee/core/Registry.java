package com.deependee.core;

public interface Registry {
    Object getValue(String key);

    void add(String key, Dependency dep);
}
