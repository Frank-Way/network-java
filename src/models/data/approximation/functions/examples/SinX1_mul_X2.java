package models.data.approximation.functions.examples;

import com.sun.istack.internal.NotNull;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;
import java.util.List;

public class SinX1_mul_X2 extends Function {
    public SinX1_mul_X2(@NotNull List<VariableRange> variableRanges) {
        super("sin(x1) * x2", variableRanges);
    }

    @Override
    public double calculate(@NotNull double[] arguments) {
        return Math.sin(arguments[0]) * arguments[1];
    }

    public static List<VariableRange> getDefaultVariableRanges() {
        return Arrays.asList(new VariableRange(0.0, 1.57),
                new VariableRange(1.0, 2.0));
    }
}
