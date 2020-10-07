package com.dependee.test.unit.parsing;

import com.deependee.Deependee;
import com.deependee.parser.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SingleDependencyTest {

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

        BinaryExpression opAdd1 = assertIsClass(BinaryExpression.class, dep.value().value());

        BigInteger val = assertIsClass(BigInteger.class, opAdd1.leftOperand().value());
        assertEquals(BigInteger.valueOf(1), val);
        assertEquals(Operator.PLUS, opAdd1.operator());

        BinaryExpression opAdd2 = assertIsClass(BinaryExpression.class, opAdd1.rightOperand().value());
        val = assertIsClass(BigInteger.class, opAdd2.leftOperand().value());
        assertEquals(BigInteger.valueOf(2), val);
        assertEquals(Operator.PLUS, opAdd2.operator());
        val = assertIsClass(BigInteger.class, opAdd2.leftOperand().value());
        assertEquals(BigInteger.valueOf(3), val);
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
        Value[] params = func.args();
        assertNotNull(params);
        assertEquals(2, params.length);
        ID param1 = assertIsClass(ID.class, params[0].value());
        assertEquals("param1", param1.text());
        String param2 = assertIsClass(String.class, params[1].value());
        assertEquals("text", param2);
    }

    private <T> T assertIsClass(Class<T> clazz, Object value) {
        assertNotNull(value);
        assertEquals(clazz, value.getClass());
        return (T)value;
    }

    private Dependency parseIDValueDependency(String text) {
        Deependee deependee = new Deependee();
        List<Object> res = deependee.interpret(text);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(Dependency.class, res.get(0).getClass());
        Dependency dep = (Dependency)res.get(0);
        assertNotNull(dep.id());
        assertNull(dep.function());
        assertNotNull(dep.value());
        assertNull(dep.externalCall());
        return dep;
    }
}
