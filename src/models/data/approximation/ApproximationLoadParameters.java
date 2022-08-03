package models.data.approximation;

import com.sun.istack.internal.NotNull;
import models.data.LoadParameters;
import models.data.approximation.functions.Function;

/**
 * Параметры метода load класса {@link ApproximationDataLoader}
 */
public class ApproximationLoadParameters extends LoadParameters {
    private final Function function;
    private final int size;
    private final int testSize;
    private final int validSize;
    private final double testPart;
    private final double validPart;
    private final double extendingFactor;

    public ApproximationLoadParameters(@NotNull Function function, int size, double testPart, double validPart, double extendingFactor) {
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
