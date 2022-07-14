package models.data.approximation.functions.examples;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.List;

public class X extends Function {
    public X(List<VariableRange> variableRanges) {
        super("x1", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return arguments[0];
    }
}
