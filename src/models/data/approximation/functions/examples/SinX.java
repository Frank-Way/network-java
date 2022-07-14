package models.data.approximation.functions.examples;

import models.data.approximation.functions.Function;
import models.data.approximation.functions.VariableRange;

import java.util.List;

public class SinX extends Function {
    public SinX(List<VariableRange> variableRanges) {
        super("sin(x1)", variableRanges);
    }

    @Override
    public double calculate(double[] arguments) {
        return Math.sin(arguments[0]);
    }
}
