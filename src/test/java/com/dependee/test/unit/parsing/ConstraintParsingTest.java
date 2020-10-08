package com.dependee.test.unit.parsing;

import com.deependee.Deependee;
import com.deependee.parser.Constraint;
import com.deependee.parser.Function;
import com.deependee.parser.Operator;
import com.dependee.test.unit.UnitTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ConstraintParsingTest extends UnitTest {
    @Test
    public void testSingleValue() {
        Constraint c = parseIDConstraint("x |= 2");

        BigInteger val = assertIsClass(BigInteger.class, c.value().value());
        assertEquals(BigInteger.valueOf(2), val);

        Operator op = assertIsClass(Operator.class, c.operator());
        assertEquals(Operator.EQUAL, op);
    }

    @Test
    public void testFunctionWithRationale() {
        Constraint c = parseIDConstraint("x |> getValue() | \"check value\"");

        Function func = assertIsClass(Function.class, c.value().value());
        assertEquals("getValue", func.name());

        Operator op = assertIsClass(Operator.class, c.operator());
        assertEquals(Operator.GREATER_THAN, op);

        assertEquals("check value", c.rationale());
    }

    private Constraint parseIDConstraint(String text) {
        Deependee deependee = new Deependee();
        List<Object> res = deependee.interpret(text);
        assertNotNull(res);
        assertEquals(1, res.size());
        Constraint c = assertIsClass(Constraint.class, res.get(0));
        assertNotNull(c.id());
        assertNull(c.function());
        assertNotNull(c.operator());
        assertNotNull(c.value());
        return c;
    }
}