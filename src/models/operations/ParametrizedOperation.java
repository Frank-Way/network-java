package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.math.MatrixOperations;

import java.util.Objects;

public abstract class ParametrizedOperation extends Operation {
    protected Matrix parameter;
    protected Matrix parameterGradient;

    protected ParametrizedOperation(@NotNull Matrix parameter) {
        super();
        this.parameter = parameter;
    }

    /***
     * copy-constructor
     */
    protected ParametrizedOperation(Matrix input,
                                 Matrix output,
                                 Matrix outputGradient,
                                 Matrix inputGradient,
                                 Matrix parameter,
                                 Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient);
        this.parameter = parameter;
        this.parameterGradient = parameterGradient;
    }

    @Override
    public Matrix backward(@NotNull Matrix outputGradient) {
        this.outputGradient = outputGradient.copy();
        MatrixOperations.assertSameShape(output, this.outputGradient);

        inputGradient = computeInputGradient(this.outputGradient);
        MatrixOperations.assertSameShape(input, inputGradient);

        parameterGradient = computeParameterGradient(this.outputGradient);
        MatrixOperations.assertSameShape(parameter, parameterGradient);

        return inputGradient;
    }

    @Override
    public abstract ParametrizedOperation copy();

    protected abstract Matrix computeParameterGradient(@NotNull Matrix outputGradient);

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
        if (!(o instanceof ParametrizedOperation)) return false;
        if (!super.equals(o)) return false;
        ParametrizedOperation that = (ParametrizedOperation) o;
        return Objects.equals(parameter, that.parameter) &&
               Objects.equals(parameterGradient, that.parameterGradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameter, parameterGradient);
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
