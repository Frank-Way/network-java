package models.losses;

import models.math.Matrix;
import models.math.MatrixOperations;
import utils.Debuggable;

import java.util.Objects;

public abstract class Loss implements Cloneable, Debuggable {
    protected Matrix prediction;
    protected Matrix target;
    protected double output;
    protected Matrix inputGradient;

    public double forward(Matrix prediction, Matrix target) {
        MatrixOperations.assertSameShape(prediction, target);

        this.prediction = prediction.clone();
        this.target = target.clone();

        output = computeOutput(this.prediction, this.target);

        return output;
    }

    public Matrix backward() {
        inputGradient = computeInputGradient(prediction, target);
        MatrixOperations.assertSameShape(prediction, inputGradient);

        return inputGradient;
    }

    protected abstract double computeOutput(Matrix prediction, Matrix target);

    protected abstract Matrix computeInputGradient(Matrix prediction, Matrix target);

    @Override
    public Loss clone() {
        try {
            Loss clone = (Loss) super.clone();
            if (prediction != null)
                clone.prediction = prediction.clone();
            if (target != null)
                clone.target = target.clone();
            if (inputGradient != null)
                clone.inputGradient = inputGradient.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loss loss = (Loss) o;
        return Double.compare(loss.output, output) == 0 && Objects.equals(prediction, loss.prediction) && Objects.equals(target, loss.target) && Objects.equals(inputGradient, loss.inputGradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prediction, target, output, inputGradient);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "prediction=" + prediction +
                ", target=" + target +
                ", output=" + output +
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
