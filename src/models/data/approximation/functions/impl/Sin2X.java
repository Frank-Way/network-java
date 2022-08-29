package models.data.approximation.functions.impl;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;

/**
 * f(x) = sin(2 * x)
 */
public class Sin2X extends Function {
    private Sin2X() {
        this(null);
    }

    public Sin2X(VariableRange[] variableRanges) {
        super("f(x1) = sin(2 * x1)", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return Math.sin(2 * arguments[0]);
    }

    public static VariableRange[] getDefaultVariableRanges() {
        return new VariableRange[]{new VariableRange(0.0, 1.57)};
    }

    @Override
    public Sin2X deepCopy() {
        return new Sin2X(Arrays.stream(variableRanges).map(VariableRange::deepCopy).toArray(VariableRange[]::new));
    }
}
