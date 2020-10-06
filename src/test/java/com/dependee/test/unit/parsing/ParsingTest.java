package com.dependee.test.unit.parsing;

import com.deependee.Deependee;
import com.deependee.parser.Dependency;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParsingTest {

    Deependee deependee = new Deependee();

    @Test
    public void testDependency() {
        List<Object> res = deependee.interpret("x <- 2");
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(Dependency.class, res.get(0).getClass());
        Dependency dep = (Dependency)res.get(0);
        assertNotNull(dep.id());
        assertNull(dep.function());
        assertNotNull(dep.value());
        assertNull(dep.externalCall());
        assertEquals(BigInteger.class, dep.value().value().getClass());
        assertEquals(BigInteger.valueOf(2), dep.value().value());
    }
}
