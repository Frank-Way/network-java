package models.data.approximation.functions.impl;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;

/**
 * f(x) = cos(sqrt(x))
 */
public class CosPiSqrtX extends Function {
    /**
     * Создание функции с диапазонами по умолчанию
     */
    public CosPiSqrtX() {
        this(new VariableRange[]{new VariableRange(0.0, 25)});
    }

    /**
     * Создание функции с указанными диапазонами
     */
    public CosPiSqrtX(VariableRange[] variableRanges) {
        super("f(x1) = cos(pi * sqrt(x1))", variableRanges);
    }

    @Override
    public double calculate(double ... arguments) {
        return Math.cos(Math.PI * Math.sqrt(arguments[0]));
    }

    @Override
    public CosPiSqrtX deepCopy() {
        return new CosPiSqrtX(Arrays.stream(variableRanges).map(VariableRange::deepCopy).toArray(VariableRange[]::new));
    }
}
