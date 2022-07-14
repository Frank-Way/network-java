package models.data.approximation.functions.examples;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.List;

public class SinX1_mul_X2 extends Function {
    public SinX1_mul_X2(List<VariableRange> variableRanges) {
        super("sin(x1) * x2", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return Math.sin(arguments[0]) * arguments[1];
    }
}
