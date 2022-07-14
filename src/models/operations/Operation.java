package models.operations;

import models.math.Matrix;
import models.math.MatrixOperations;
import utils.Debuggable;

import java.util.Objects;

public abstract class Operation implements Cloneable, Debuggable {
    protected Matrix input;
    protected Matrix output;
    protected Matrix outputGradient;
    protected Matrix inputGradient;

    public Matrix forward(Matrix input) {
        this.input = input.clone();
        output = computeOutput(this.input);

        return output;
    };

    public Matrix backward(Matrix outputGradient) {
        this.outputGradient = outputGradient.clone();
        MatrixOperations.assertSameShape(output, this.outputGradient);

        inputGradient = computeInputGradient(this.outputGradient);
        MatrixOperations.assertSameShape(input, inputGradient);

        return inputGradient;
    };

    protected abstract Matrix computeOutput(Matrix input);
    protected abstract Matrix computeInputGradient(Matrix outputGradient);

    @Override
    public Operation clone() {
        try {
            Operation clone = (Operation) super.clone();
            if (input != null)
                clone.input = input.clone();
            if (output != null)
                clone.output = output.clone();
            if (inputGradient != null)
                clone.inputGradient = inputGradient.clone();
            if (outputGradient != null)
                clone.outputGradient = outputGradient.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return input.equals(operation.input) && output.equals(operation.output) && outputGradient.equals(operation.outputGradient) && inputGradient.equals(operation.inputGradient);
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
