package com.dependee.test.unit.parsing;

import com.deependee.core.parser.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MultiLineParsingTest extends com.dependee.test.unit.UnitTest {

    @Test
    public void testMultiLineEmptyObject() {
        Dependency dep = parseIDValueDependency("x <- {\n}");

        Obj obj = assertIsClass(Obj.class, dep.value().value());
        Pair[] pairs = assertNotNull(obj.keyValuePairs());
        assertEquals(0, pairs.length);

    }

    @Test
    public void testMultiLineObject() {
        Dependency dep = parseIDValueDependency("x <- { \n\t x:2 \n\t , y:3 \n}");

        Obj obj = assertIsClass(Obj.class, dep.value().value());
        Pair[] pairs = assertNotNull(obj.keyValuePairs());
        assertEquals(2, pairs.length);

        Pair pair = assertNotNull(pairs[0]);
        Object key = assertNotNull(pair.key());
        Value value = assertNotNull(pair.value());
        ID keyID = assertIsClass(ID.class, key);
        assertEquals(KeyType.ID, pair.keyType());
        assertEquals("x", keyID.text());
        BigInteger val = assertIsClass(BigInteger.class, value.value());
        assertEquals(BigInteger.valueOf(2), val);

        pair = assertNotNull(pairs[1]);
        key = assertNotNull(pair.key());
        value = assertNotNull(pair.value());
        keyID = assertIsClass(ID.class, key);
        assertEquals(KeyType.ID, pair.keyType());
        assertEquals("y", keyID.text());
        val = assertIsClass(BigInteger.class, value.value());
        assertEquals(BigInteger.valueOf(3), val);
    }

    @Test
    public void testMultilineEmptyArray() {
        Dependency dep = parseIDValueDependency("x <- [\n]");

        Array array = assertIsClass(Array.class, dep.value().value());

        Value[] elements = assertNotNull(array.elements());
        assertEquals(0, elements.length);
    }
    @Test

    public void testMultilineArray() {
        Dependency dep = parseIDValueDependency("x <- [1,\n\t \"text\",\r\n\t y,\n doSomething()]");

        Array array = assertIsClass(Array.class, dep.value().value());

        Value[] elements = assertNotNull(array.elements());
        assertEquals(4, elements.length);

        Value element = assertNotNull(elements[0]);
        BigInteger val = assertIsClass(BigInteger.class, element.value());
        assertEquals(BigInteger.valueOf(1), val);

        element = assertNotNull(elements[1]);
        String str = assertIsClass(String.class, element.value());
        assertEquals("text", str);

        element = assertNotNull(elements[2]);
        ID id = assertIsClass(ID.class, element.value());
        assertEquals("y", id.text());

        element = assertNotNull(elements[3]);
        Function func = assertIsClass(Function.class, element.value());
        assertEquals("doSomething", func.name());
        assertEquals(0, func.args().length);
    }

    private Dependency parseIDValueDependency(String text) {
        DeependeeParser parser = new DeependeeParser();
        List<Object> res = parser.parse(text);
        assertNotNull(res);
        assertEquals(1, res.size());
        Dependency dep = assertIsClass(Dependency.class, res.get(0));
        assertNotNull(dep.id());
        assertNull(dep.function());
        assertNotNull(dep.value());
        assertNull(dep.externalCall());
        return dep;
    }
}
