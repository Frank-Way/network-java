package models.data.approximation.functions.impl;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;

/**
 * f(x) = sin(x)
 */
public class SinX extends Function {
    private SinX() {
        this(null);
    }

    public SinX(VariableRange[] variableRanges) {
        super("f(x1) = sin(x1)", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return Math.sin(arguments[0]);
    }

    public static VariableRange[] getDefaultVariableRanges() {
        return new VariableRange[]{new VariableRange(0.0, 1.57)};
    }

    @Override
    public SinX deepCopy() {
        return new SinX(Arrays.stream(variableRanges).map(VariableRange::deepCopy).toArray(VariableRange[]::new));
    }
}
