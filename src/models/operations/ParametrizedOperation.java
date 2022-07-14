package models.operations;

import models.math.Matrix;
import models.math.MatrixOperations;

import java.util.Objects;

public abstract class ParametrizedOperation extends Operation {
    protected Matrix parameter;
    protected Matrix parameterGradient;

    protected ParametrizedOperation(Matrix parameter) {
        this.parameter = parameter;
    }

    @Override
    public Matrix backward(Matrix outputGradient) {
        this.outputGradient = outputGradient.clone();
        MatrixOperations.assertSameShape(output, this.outputGradient);

        inputGradient = computeInputGradient(this.outputGradient);
        MatrixOperations.assertSameShape(input, inputGradient);

        parameterGradient = computeParameterGradient(this.outputGradient);
        MatrixOperations.assertSameShape(parameter, parameterGradient);

        return inputGradient;
    }

    @Override
    public ParametrizedOperation clone() {
        ParametrizedOperation clone = (ParametrizedOperation) super.clone();
        if (parameter != null)
            clone.parameter = parameter.clone();
        if (parameterGradient != null)
            clone.parameterGradient = parameterGradient.clone();
        return clone;
    }

    protected abstract Matrix computeParameterGradient(Matrix outputGradient);

    public Matrix getParameter() {
        return parameter;
    }

    public Matrix getParameterGradient() {
        return parameterGradient;
    }

    public void setParameter(Matrix parameter) {
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ParametrizedOperation that = (ParametrizedOperation) o;
        return getParameter().equals(that.getParameter()) && getParameterGradient().equals(that.getParameterGradient());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParameter(), getParameterGradient());
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", outputGradient=" + outputGradient +
                ", inputGradient=" + inputGradient +
                ", parameter=" + parameter +
                ", parameterGradient=" + parameterGradient +
                '}';
    }
}
