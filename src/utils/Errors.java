package utils;

import models.interfaces.Copyable;
import models.math.Matrix;

public class Errors implements Copyable<Errors> {
    private final double maxAbsoluteError;
    private final double maxRelativeError;
    private final double meanAbsoluteError;

    public Errors(double maxAbsoluteError, double maxRelativeError, double meanAbsoluteError) {
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxRelativeError;
        this.meanAbsoluteError = meanAbsoluteError;
    }

    public static Errors buildFromTargetsAndPredictions(Matrix targets, Matrix predictions) {
        Matrix errors = predictions.sub(targets).abs();
        double maxAbsoluteError = errors.max();
        return new Errors(maxAbsoluteError,
                maxAbsoluteError / (targets.max() - targets.min()) * 100,
                errors.sum() / errors.size());
    }

    public double getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    public double getMaxRelativeError() {
        return maxRelativeError;
    }

    public double getMeanAbsoluteError() {
        return meanAbsoluteError;
    }

    @Override
    public Errors copy() {
        return new Errors(maxAbsoluteError, maxRelativeError, meanAbsoluteError);
    }

    @Override
    public String toString() {
        return "Errors{" +
                "maxAbsoluteError=" + maxAbsoluteError +
                ", maxRelativeError=" + maxRelativeError +
                ", meanAbsoluteError=" + meanAbsoluteError +
                '}';
    }
}
