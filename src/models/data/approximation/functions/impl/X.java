package models.data.approximation.functions.impl;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;

/**
 * f(x) = x
 */
public class X extends Function {
    private X() {
        this(null);
    }

    public X(VariableRange[] variableRanges) {
        super("f(x1) = x1", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return arguments[0];
    }

    public static VariableRange[] getDefaultVariableRanges() {
        return new VariableRange[]{new VariableRange(1.0, 2.0)};
    }

    @Override
    public X deepCopy() {
        return new X(Arrays.stream(variableRanges).map(VariableRange::deepCopy).toArray(VariableRange[]::new));
    }
}
