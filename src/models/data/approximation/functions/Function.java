package models.data.approximation.functions;

import com.sun.istack.internal.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public abstract class Function {
    private final String expression;
    private final List<VariableRange> variableRanges;

    public Function(@NotNull String expression, @NotNull List<VariableRange> variableRanges) {
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

    public abstract double calculate(@NotNull double[] arguments);

    protected static List<VariableRange> getDefaultVariableRanges() {
        throw new NotImplementedException();
    }
}
