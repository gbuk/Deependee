package com.dependee.test.unit.parsing;

import com.deependee.core.parser.DeependeeParser;
import com.deependee.core.parser.Dependency;
import com.dependee.test.unit.UnitTest;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleDependenciesParsingTest extends UnitTest {

    @Test
    public void testTwoDependencies() {
        List<Object> deps = parse("x <- 2 \n \n \n y<-3");

        assertEquals(2, deps.size());

        Dependency dep = assertIsClass(Dependency.class, deps.get(0));
        BigInteger val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(2), val);

        dep = assertIsClass(Dependency.class, deps.get(1));
        val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(3), val);
    }

    @Test
    public void testThreeDependencies() {
        List<Object> deps = parse("x <- 2 \n z<- 4 \n y<-3");

        assertEquals(3, deps.size());

        Dependency dep = assertIsClass(Dependency.class, deps.get(0));
        BigInteger val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(2), val);

        dep = assertIsClass(Dependency.class, deps.get(1));
        val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(4), val);

        dep = assertIsClass(Dependency.class, deps.get(2));
        val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(3), val);
    }

    private List<Object> parse(String text) {
        DeependeeParser parser = new DeependeeParser();
        List<Object> res = parser.parse(text);
        assertNotNull(res);
        return res;
    }
}
