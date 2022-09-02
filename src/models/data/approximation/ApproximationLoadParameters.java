package models.data.approximation;

import models.data.LoadParameters;
import models.data.approximation.functions.Function;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.util.Arrays;

/**
 * Параметры метода load класса {@link ApproximationDataLoader}
 */
@YamlSerializable
public class ApproximationLoadParameters extends LoadParameters {
    @YamlField private final Function function;
    @YamlField private final int size;
    @YamlField private final int testSize;
    @YamlField private final int validSize;
    @YamlField private final double[] extendingFactors;

    private ApproximationLoadParameters() {
        this(null, 0, 0, 0, null);
    }

    public ApproximationLoadParameters(Function function, int size, int testSize, int validSize, double[] extendingFactors) {
        this.function = function;
        this.size = size;
        this.testSize = testSize;
        this.validSize = validSize;
        this.extendingFactors = extendingFactors;
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

    public double[] getExtendingFactors() {
        return extendingFactors;
    }

    public static ApproximationLoadParametersBuilder newBuilder() {
        return new ApproximationLoadParametersBuilder();
    }

    @Override
    public String toString() {
        return "ApproximationLoadParameters{" +
                "function=" + function +
                ", size=" + size +
                ", testSize=" + testSize +
                ", validSize=" + validSize +
                ", extendingFactors=" + Arrays.toString(extendingFactors) +
                '}';
    }

    @Override
    public ApproximationLoadParameters deepCopy() {
        return new ApproximationLoadParameters(function.deepCopy(), size, testSize, validSize,
                Arrays.copyOf(extendingFactors, extendingFactors.length));
    }
}
