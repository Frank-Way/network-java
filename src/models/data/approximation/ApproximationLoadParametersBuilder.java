package models.data.approximation;

import models.data.approximation.functions.Function;
import serialization.annotations.YamlField;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ApproximationLoadParametersBuilder {
    protected Function function;
    protected int size;
    protected double testPart;
    protected int testSize;
    protected double validPart;
    protected int validSize;
    protected double extendingFactor;
    protected double[] extendingFactors;

    public ApproximationLoadParametersBuilder() {}

    public ApproximationLoadParametersBuilder function(Function function) {
        this.function = function;
        return this;
    }

    public ApproximationLoadParametersBuilder size(int size) {
        this.size = size;
        return this;
    }

    public ApproximationLoadParametersBuilder testPart(double testPart) {
        this.testPart = testPart;
        return this;
    }

    public ApproximationLoadParametersBuilder testSize(int testSize) {
        this.testSize = testSize;
        return this;
    }

    public ApproximationLoadParametersBuilder validPart(double validPart) {
        this.validPart = validPart;
        return this;
    }

    public ApproximationLoadParametersBuilder validSize(int validSize) {
        this.validSize = validSize;
        return this;
    }

    public ApproximationLoadParametersBuilder extendingFactor(double extendingFactor) {
        this.extendingFactor = extendingFactor;
        return this;
    }

    public ApproximationLoadParametersBuilder extendingFactors(double[] extendingFactors) {
        this.extendingFactors = extendingFactors;
        return this;
    }

    public ApproximationLoadParameters build() {
        validate();
        prepare();
        return new ApproximationLoadParameters(function, size, testSize, validSize, extendingFactors);
    }

    protected void validate() {
        if (function == null || size < 1 ||
                (testSize < 1 && size * testPart < 1) ||
                (validSize < 1 && size * validPart < 1) ||
                (extendingFactors == null && extendingFactor < 1.0))
            throw new IllegalStateException("Некорректное состояние билдера: " + this);
    }

    protected void prepare() {
        if (testSize == 0)
            testSize = (int) (size * testPart);
        if (validSize == 0)
            validSize = (int) (size * validPart);
        if (extendingFactors == null) {
            extendingFactor = Math.max(extendingFactor, 1.0);
            int inputsCount = function.getInputsCount();
            extendingFactors = IntStream.range(0, inputsCount).mapToDouble(i -> extendingFactor).toArray();
        }
    }

    @Override
    public String toString() {
        return "ApproximationLoadParametersBuilder{" +
                "function=" + function +
                ", size=" + size +
                ", testPart=" + testPart +
                ", testSize=" + testSize +
                ", validPart=" + validPart +
                ", validSize=" + validSize +
                ", extendingFactor=" + extendingFactor +
                ", extendingFactors=" + Arrays.toString(extendingFactors) +
                '}';
    }
}
