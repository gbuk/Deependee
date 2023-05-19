package com.dependee.test.unit.parsing;

import com.deependee.core.parser.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SingleDependencyParsingTest extends com.dependee.test.unit.UnitTest {

    @Test
    public void testSingleValue() {
        Dependency dep = parseIDValueDependency("x <- 2");

        BigInteger val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(2), val);
    }

    @Test
    public void testSingleValueNestedBrackets() {
        Dependency dep = parseIDValueDependency("x <- ((-2))");

        BigInteger val = assertIsClass(BigInteger.class, dep.value().value());
        assertEquals(BigInteger.valueOf(-2), val);
    }

    @Test
    public void testSimpleOperation() {
        Dependency dep = parseIDValueDependency("x <- 1.0 + 2.1");

        BinaryExpression op = assertIsClass(BinaryExpression.class, dep.value().value());

        BigDecimal val = assertIsClass(BigDecimal.class, op.leftOperand().value());
        assertEquals(BigDecimal.valueOf(1.0), val);
        assertEquals(Operator.PLUS, op.operator());
        val = assertIsClass(BigDecimal.class, op.rightOperand().value());
        assertEquals(BigDecimal.valueOf(2.1), val);
    }

    @Test
    public void testCompositeOperation() {
        Dependency dep = parseIDValueDependency("x <- 1 + 2 + 3");

        BinaryExpression opSecondAdd = assertIsClass(BinaryExpression.class, dep.value().value());

        BigInteger val = assertIsClass(BigInteger.class, opSecondAdd.rightOperand().value());
        assertEquals(BigInteger.valueOf(3), val);
        assertEquals(Operator.PLUS, opSecondAdd.operator());

        BinaryExpression opFirstAdd = assertIsClass(BinaryExpression.class, opSecondAdd.leftOperand().value());
        val = assertIsClass(BigInteger.class, opFirstAdd.leftOperand().value());
        assertEquals(BigInteger.valueOf(1), val);
        assertEquals(Operator.PLUS, opFirstAdd.operator());
        val = assertIsClass(BigInteger.class, opFirstAdd.rightOperand().value());
        assertEquals(BigInteger.valueOf(2), val);
    }

    @Test
    public void testNestedOperations() {
        Dependency dep = parseIDValueDependency("x <- ((1.0 + 2.1) * (1.3))");

        BinaryExpression opMult = assertIsClass(BinaryExpression.class, dep.value().value());
        BinaryExpression opAdd = assertIsClass(BinaryExpression.class, opMult.leftOperand().value());

        assertEquals(Operator.TIMES, opMult.operator());
        BigDecimal val = assertIsClass(BigDecimal.class, opMult.rightOperand().value());
        assertEquals(BigDecimal.valueOf(1.3), val);

        val = assertIsClass(BigDecimal.class, opAdd.leftOperand().value());
        assertEquals(BigDecimal.valueOf(1.0), val);
        assertEquals(Operator.PLUS, opAdd.operator());
        val = assertIsClass(BigDecimal.class, opAdd.rightOperand().value());
        assertEquals(BigDecimal.valueOf(2.1), val);
    }

    @Test
    public void testSimpleFunction() {
        Dependency dep = parseIDValueDependency("x <- doSomething()");

        Function func = assertIsClass(Function.class, dep.value().value());
        assertEquals("doSomething", func.name());
        assertNotNull(func.args());
        assertEquals(0, func.args().length);
    }

    @Test
    public void testFunctionWithParameters() {
        Dependency dep = parseIDValueDependency("x <- doSomething(param1, \"text\")");

        Function func = assertIsClass(Function.class, dep.value().value());
        assertEquals("doSomething", func.name());
        Value[] params = assertNotNull(func.args());
        assertEquals(2, params.length);
        ID param1 = assertIsClass(ID.class, params[0].value());
        assertEquals("param1", param1.text());
        String param2 = assertIsClass(String.class, params[1].value());
        assertEquals("text", param2);
    }

    @Test
    public void testEmptyArray() {
        Dependency dep = parseIDValueDependency("x <- []");

        Array array = assertIsClass(Array.class, dep.value().value());

        Value[] elements = assertNotNull(array.elements());
        assertEquals(0, elements.length);
    }

    @Test
    public void testSimpleArray() {
        Dependency dep = parseIDValueDependency("x <- [1, \"text\", y, doSomething()]");

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

    @Test
    public void testEmptyObject() {
        Dependency dep = parseIDValueDependency("x <- {}");

        Obj obj = assertIsClass(Obj.class, dep.value().value());

        Pair[] pairs = assertNotNull(obj.keyValuePairs());
        assertEquals(0, pairs.length);
    }

    @Test
    public void testSimpleObject() {
        Dependency dep = parseIDValueDependency("x <- { y: 1, \"key_name\": \"text\", z: t, getSomething(): doSomething()}");

        Obj obj = assertIsClass(Obj.class, dep.value().value());

        Pair[] pairs = assertNotNull(obj.keyValuePairs());
        assertEquals(4, pairs.length);

        Pair pair = assertNotNull(pairs[0]);
        Object key = assertNotNull(pair.key());
        Value value = assertNotNull(pair.value());
        ID keyID = assertIsClass(ID.class, key);
        assertEquals(KeyType.ID, pair.keyType());
        assertEquals("y", keyID.text());
        BigInteger val = assertIsClass(BigInteger.class, value.value());
        assertEquals(BigInteger.valueOf(1), val);

        pair = assertNotNull(pairs[1]);
        key = assertNotNull(pair.key());
        value = assertNotNull(pair.value());
        String keyString = assertIsClass(String.class, key);
        assertEquals(KeyType.STRING, pair.keyType());
        assertEquals("key_name", keyString);
        String str = assertIsClass(String.class, value.value());
        assertEquals("text", str);

        pair = assertNotNull(pairs[2]);
        key = assertNotNull(pair.key());
        value = assertNotNull(pair.value());
        keyID = assertIsClass(ID.class, key);
        assertEquals(KeyType.ID, pair.keyType());
        assertEquals("z", keyID.text());
        ID id = assertIsClass(ID.class, value.value());
        assertEquals("t", id.text());

        pair = assertNotNull(pairs[3]);
        key = assertNotNull(pair.key());
        value = assertNotNull(pair.value());
        Function keyFunction = assertIsClass(Function.class, key);
        assertEquals("getSomething", keyFunction.name());
        Function func = assertIsClass(Function.class, value.value());
        assertEquals("doSomething", func.name());
        assertEquals(0, func.args().length);
    }

    @Test
    public void testSimpleBoolean() {
        Dependency dep = parseIDValueDependency("x <- true");

        Boolean val = assertIsClass(Boolean.class, dep.value().value());
        assertEquals(Boolean.TRUE, val);
    }

    @Test
    public void testBooleanNot() {
        Dependency dep = parseIDValueDependency("x <- !false");

        UnaryExpression op = assertIsClass(UnaryExpression.class, dep.value().value());
        assertEquals(Operator.NOT, op.operator());
        Boolean val = assertIsClass(Boolean.class, op.operand().value());
        assertEquals(Boolean.FALSE, val);
    }

    @Test
    public void testSimpleAccessor() {
        Dependency dep = parseIDValueDependency("x <- y.z()");

        BinaryExpression op = assertIsClass(BinaryExpression.class, dep.value().value());
        ID id = assertIsClass(ID.class, op.leftOperand().value());
        assertEquals("y", id.text());
        assertEquals(Operator.ACCESSOR, op.operator());
        Function func = assertIsClass(Function.class, op.rightOperand().value());
        assertEquals("z", func.name());
    }

    @Test
    public void testObjectAccessor() {
        Dependency dep = parseIDValueDependency("x <- { }.\"key\"");

        BinaryExpression op = assertIsClass(BinaryExpression.class, dep.value().value());
        Obj obj = assertIsClass(Obj.class, op.leftOperand().value());
        assertEquals(0, obj.keyValuePairs().length);
        assertEquals(Operator.ACCESSOR, op.operator());
        String str = assertIsClass(String.class, op.rightOperand().value());
        assertEquals("key", str);
    }

    @Test
    public void testTernaryOperator() {
        Dependency dep = parseIDValueDependency("x <- (y=2)?\"yes\":no()");

        TernaryExpression op = assertIsClass(TernaryExpression.class, dep.value().value());

        BinaryExpression test = assertIsClass(BinaryExpression.class, op.leftOperand().value());
        ID id = assertIsClass(ID.class, test.leftOperand().value());
        assertEquals("y", id.text());
        BigInteger val = assertIsClass(BigInteger.class, test.rightOperand().value());
        assertEquals(BigInteger.TWO, val);

        assertEquals(Operator.LEFT_TERNARY_OPERATOR, op.firstOperator());

        String yes = assertIsClass(String.class, op.middleOperand().value());
        assertEquals("yes", yes);

        assertEquals(Operator.RIGHT_TERNARY_OPERATOR, op.secondOperator());

        Function func = assertIsClass(Function.class, op.rightOperand().value());
        assertEquals("no", func.name());
    }

    @Test
    public void testArrayAccessor() {
        Dependency dep = parseIDValueDependency("x <- [ ].do()");

        BinaryExpression op = assertIsClass(BinaryExpression.class, dep.value().value());
        Array array = assertIsClass(Array.class, op.leftOperand().value());
        assertEquals(0, array.elements().length);
        assertEquals(Operator.ACCESSOR, op.operator());
        Function func = assertIsClass(Function.class, op.rightOperand().value());
        assertEquals("do", func.name());
    }

    @Test
    public void testComparison() {
        Dependency dep = parseIDValueDependency("x <- y <= 2");
        BinaryExpression op = assertIsClass(BinaryExpression.class, dep.value().value());
        ID id = assertIsClass(ID.class, op.leftOperand().value());
        assertEquals("y", id.text());
        assertEquals(Operator.LESS_THAN_OR_EQUAL, op.operator());
        BigInteger val = assertIsClass(BigInteger.class, op.rightOperand().value());
        assertEquals(BigInteger.TWO, val);
    }

    @Test
    public void testString() {
        //TODO: this test is supposed to check emoji parsing
        //      but the extended unicode is not supported yet
        Dependency dep = parseIDValueDependency("x <- \"dream\\u5922\\t\\u1f601smiley\"");
        String str = assertIsClass(String.class, dep.value().value());
        assertEquals("dream\u5922\t\u1f601smiley", str);
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
