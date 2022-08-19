package models.data.approximation.functions.examples;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;
import utils.copy.CopyUtils;

import java.util.Arrays;

/**
 * f(x, y) = y * sin(x)
 */
public class SinX1_mul_X2 extends Function {
    private SinX1_mul_X2() {
        this(null);
    }

    public SinX1_mul_X2(VariableRange[] variableRanges) {
        super("f(x1, x2) = sin(x1) * x2", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return Math.sin(arguments[0]) * arguments[1];
    }

    public static VariableRange[] getDefaultVariableRanges() {
        return new VariableRange[]{new VariableRange(0.0, 1.57),
                new VariableRange(1.0, 2.0)};
    }

    @Override
    public SinX1_mul_X2 deepCopy() {
        return new SinX1_mul_X2(Arrays.stream(variableRanges).map(VariableRange::deepCopy).toArray(VariableRange[]::new));
    }
}
