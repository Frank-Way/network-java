package models.data.approximation;

import models.data.LoadParameters;
import models.data.approximation.functions.Function;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;

/**
 * Параметры метода load класса {@link ApproximationDataLoader}
 */
@YamlSerializable
public class ApproximationLoadParameters extends LoadParameters {
    @YamlField private final Function function;
    @YamlField private final int size;
    @YamlField private final int testSize;
    @YamlField private final int validSize;
    @YamlField private final double extendingFactor;

    private ApproximationLoadParameters() {
        this(null, 0, 0, 0, 0);
    }

    public ApproximationLoadParameters(Function function, int size, double testPart, double validPart, double extendingFactor) {
        this.function = function;
        this.size = size;
        this.testSize = (int)(size * testPart);
        this.validSize = (int)(size * validPart);
        this.extendingFactor = extendingFactor;
    }

    public ApproximationLoadParameters(Function function, int size, int testSize, int validSize, double extendingFactor) {
        this.function = function;
        this.size = size;
        this.testSize = testSize;
        this.validSize = validSize;
        this.extendingFactor = extendingFactor;
    }

    public Function getFunction() {
        return function;
    }

    public int getSize() {
        return size;
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
                '}';
    }

    @Override
    public ApproximationLoadParameters deepCopy() {
        return new ApproximationLoadParameters(function.deepCopy(), size, testSize, validSize, extendingFactor);
    }
}
