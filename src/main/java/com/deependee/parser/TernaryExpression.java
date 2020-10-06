package com.deependee.parser;

public record TernaryExpression(Value leftOperand, Operator firstOperator, Value middleOperand, Operator secondOperator, Value rightOperand) {
}
