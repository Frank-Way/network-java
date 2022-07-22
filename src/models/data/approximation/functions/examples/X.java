package models.data.approximation.functions.examples;

import com.sun.istack.internal.NotNull;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.Arrays;
import java.util.List;

public class X extends Function {
    public X(@NotNull List<VariableRange> variableRanges) {
        super("x1", variableRanges);
    }

    @Override
    public double calculate(@NotNull double[] arguments) {
        return arguments[0];
    }

    public static List<VariableRange> getDefaultVariableRanges() {
        return Arrays.asList(new VariableRange(1.0, 2.0));
    }
}
