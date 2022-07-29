package models.losses;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.interfaces.Debuggable;

import java.io.Serializable;
import java.util.Objects;

public abstract class Loss implements Copyable<Loss>, Debuggable, Serializable {
    protected Matrix prediction;
    protected Matrix target;
    protected double output;
    protected Matrix inputGradient;

    public Loss() {}

    /***
     * copy-constructor
     */
    protected Loss(Matrix prediction, Matrix target, double output, Matrix inputGradient) {
        this.prediction = prediction;
        this.target = target;
        this.output = output;
        this.inputGradient = inputGradient;
    }

    public double forward(@NotNull Matrix prediction, @NotNull Matrix target) {
        MatrixOperations.assertSameShape(prediction, target);

        this.prediction = prediction.copy();
        this.target = target.copy();

        output = computeOutput(this.prediction, this.target);

        return output;
    }

    public Matrix backward() {
        inputGradient = computeInputGradient(prediction, target);
        MatrixOperations.assertSameShape(prediction, inputGradient);

        return inputGradient;
    }

    public void clear() {
        prediction = null;
        target = null;
        inputGradient = null;
        output = 0.0;
    }

    protected abstract double computeOutput(@NotNull Matrix prediction, @NotNull Matrix target);

    protected abstract Matrix computeInputGradient(@NotNull Matrix prediction, @NotNull Matrix target);

    @Override
    public abstract Loss copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loss loss = (Loss) o;
        return Double.compare(output, loss.output) == 0 &&
               Objects.equals(prediction, loss.prediction) &&
               Objects.equals(target, loss.target) &&
               Objects.equals(inputGradient, loss.inputGradient);
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

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();
}
