package com.deependee;

import java.util.HashMap;
import java.util.Map;

public class BasicRegistryImpl implements Registry {

    private Map<String, Object> registry = new HashMap<>();
    private Map<String, Constraint> constraints = new HashMap<>();

    @Override
    public Object getValue(String key) {
        Object obj = registry.get(key);
        if (obj == null)
            throw new NullPointerException(key);
        if (obj instanceof Dependency x_obj)
            return x_obj.resolve();
        throw new RuntimeException(key + " is not a computable dependency: " + obj.getClass().getCanonicalName());
    }

    public Object getGraph(String key) {
        Object obj = registry.get(key);
        if (obj == null)
            throw new NullPointerException(key);
        if (obj instanceof Dependency x_obj)
            return x_obj.trace();
        throw new RuntimeException(key + " is not a computable dependency: " + obj.getClass().getCanonicalName());
    }

    public Dependency getDependency(String key) {
        if (registry.containsKey(key))
            return (Dependency)registry.get(key);
        return new DeferredDependency();
    }

    @Override
    public void add(String key, Dependency dep) {
        registry.put(key, dep);
    }
}
