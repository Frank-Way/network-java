package models.data.approximation.functions;

import java.util.List;

public abstract class Function {
    protected final String expression;
    protected final List<VariableRange> variableRanges;

    public Function(String expression, List<VariableRange> variableRanges) {
        this.expression = expression;
        this.variableRanges = variableRanges;
    }

    public int getInputsCount() {
        return variableRanges.size();
    }

    public String getExpression() {
        return expression;
    }

    public List<VariableRange> getVariableRanges() {
        return variableRanges;
    }

    public abstract double calculate(double[] arguments);
}
