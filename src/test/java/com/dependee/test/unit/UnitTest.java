package com.dependee.test.unit;

import org.junit.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    @SuppressWarnings("unchecked")
    protected <T> T assertIsClass(Class<T> clazz, Object value) {
        assertNotNull(value);
        assertEquals(clazz, value.getClass());
        return (T) value;
    }

    protected <T> T assertNotNull(T value) {
        Assert.assertNotNull(value);
        return value;
    }
}
