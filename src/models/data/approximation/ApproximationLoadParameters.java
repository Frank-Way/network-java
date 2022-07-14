package models.data.approximation;

import models.data.LoadParameters;
import models.data.approximation.functions.Function;

public class ApproximationLoadParameters extends LoadParameters {
    protected final Function function;
    protected final int size;
    protected final int testSize;
    protected final int validSize;
    protected final double testPart;
    protected final double validPart;
    protected final double extendingFactor;

    public ApproximationLoadParameters(Function function, int size, double testPart, double validPart) {
        this.function = function;
        this.size = size;
        this.testSize = (int)(size * testPart);
        this.validSize = (int)(size * validPart);
        this.testPart = testPart;
        this.validPart = validPart;
        extendingFactor = 1.0;
    }

    public ApproximationLoadParameters(Function function, int size, double testPart, double validPart, double extendingFactor) {
        this.function = function;
        this.size = size;
        this.testSize = (int)(size * testPart);
        this.validSize = (int)(size * validPart);
        this.testPart = testPart;
        this.validPart = validPart;
        this.extendingFactor = extendingFactor;
    }

    public Function getFunction() {
        return function;
    }

    public int getSize() {
        return size;
    }

    public double getTestPart() {
        return testPart;
    }

    public double getValidPart() {
        return validPart;
    }

    public int getTestSize() {
        return testSize;
    }

    public int getValidSize() {
        return validSize;
    }

    public double getExtendingFactor() {
        return extendingFactor;
    }

    @Override
    public String toString() {
        return "ApproximationLoadParameters{" +
                "function=" + function +
                ", size=" + size +
                ", testSize=" + testSize +
                ", validSize=" + validSize +
                ", testPart=" + testPart +
                ", validPart=" + validPart +
                '}';
    }
}
