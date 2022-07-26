package models.data.approximation.functions.examples;

import com.sun.istack.internal.NotNull;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;
import java.util.List;

public class Sin2X extends Function {
    public Sin2X(@NotNull List<VariableRange> variableRanges) {
        super("sin(2 * x1)", variableRanges);
    }

    @Override
    public double calculate(@NotNull double[] arguments) {
        return Math.sin(2 * arguments[0]);
    }

    public static List<VariableRange> getDefaultVariableRanges() {
        return Arrays.asList(new VariableRange(0.0, 1.57));
    }
}