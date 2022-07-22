package models.operations;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.interfaces.Debuggable;

import java.util.Objects;

public abstract class Operation implements Copyable<Operation>, Debuggable {
    protected Matrix input;
    protected Matrix output;
    protected Matrix outputGradient;
    protected Matrix inputGradient;

    public Operation() {}

    /***
     * copy-constructor
     */
    protected Operation(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        this.input = input;
        this.output = output;
        this.outputGradient = outputGradient;
        this.inputGradient = inputGradient;
    }

    public Matrix forward(@NotNull Matrix input) {
        this.input = input.copy();
        output = computeOutput(this.input);

        return output;
    };

    public Matrix backward(@NotNull Matrix outputGradient) {
        this.outputGradient = outputGradient.copy();
        MatrixOperations.assertSameShape(output, this.outputGradient);

        inputGradient = computeInputGradient(this.outputGradient);
        MatrixOperations.assertSameShape(input, inputGradient);

        return inputGradient;
    };

    protected abstract Matrix computeOutput(@NotNull Matrix input);

    protected abstract Matrix computeInputGradient(@NotNull Matrix outputGradient);

    @Override
    public abstract Operation copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operation)) return false;
        Operation operation = (Operation) o;
        return Objects.equals(input, operation.input) &&
               Objects.equals(output, operation.output) &&
               Objects.equals(outputGradient, operation.outputGradient) &&
               Objects.equals(inputGradient, operation.inputGradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, outputGradient, inputGradient);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", outputGradient=" + outputGradient +
                ", inputGradient=" + inputGradient +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();
}
