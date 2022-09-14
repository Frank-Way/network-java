package models.data.approximation;

import models.data.approximation.functions.Function;
import utils.copy.DeepCopyable;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Билдер для {@link ApproxLoadParameters}, позволяющий не указывать некоторые параметры выборки и
 * использовать значения по умолчанию, также указывать размер тестовой (и валидационной) выборки как часть от
 * обучающей выборки или в абсолютных единицах, а также указывать одно значение в качестве коэффициента расширения.
 */
public class ApproxLoadParametersBuilder implements DeepCopyable {
    private static final double defaultTestPart = 0.5;
    private static final double defaultValidPart = 0.25;
    private static final double defaultExtendingFactor = 1.0;

    private Function function;

    private int size;
    private int[] sizes;

    private double testPart;
    private double[] testParts;
    private int testSize;
    private int[] testSizes;

    private double validPart;
    private double[] validParts;
    private int validSize;
    private int[] validSizes;

    private double extendingFactor;
    private double[] extendingFactors;

    public ApproxLoadParametersBuilder() {}

    public ApproxLoadParametersBuilder function(Function function) {
        this.function = function;
        return this;
    }

    public ApproxLoadParametersBuilder size(int size) {
        this.size = size;
        return this;
    }

    public ApproxLoadParametersBuilder sizes(int[] sizes) {
        this.sizes = sizes;
        return this;
    }

    public ApproxLoadParametersBuilder testPart(double testPart) {
        this.testPart = testPart;
        return this;
    }

    public ApproxLoadParametersBuilder testParts(double[] testParts) {
        this.testParts = testParts;
        return this;
    }

    public ApproxLoadParametersBuilder testSize(int testSize) {
        this.testSize = testSize;
        return this;
    }

    public ApproxLoadParametersBuilder testSizes(int[] testSizes) {
        this.testSizes = testSizes;
        return this;
    }

    public ApproxLoadParametersBuilder validPart(double validPart) {
        this.validPart = validPart;
        return this;
    }

    public ApproxLoadParametersBuilder validParts(double[] validParts) {
        this.validParts = validParts;
        return this;
    }

    public ApproxLoadParametersBuilder validSize(int validSize) {
        this.validSize = validSize;
        return this;
    }

    public ApproxLoadParametersBuilder validSizes(int[] validSizes) {
        this.validSizes = validSizes;
        return this;
    }

    public ApproxLoadParametersBuilder extendingFactor(double extendingFactor) {
        this.extendingFactor = extendingFactor;
        return this;
    }

    public ApproxLoadParametersBuilder extendingFactors(double[] extendingFactors) {
        this.extendingFactors = extendingFactors;
        return this;
    }

    public int getSize() {
        return size;
    }

    public int[] getSizes() {
        return sizes;
    }

    public double getExtendingFactor() {
        return extendingFactor;
    }

    public double[] getExtendingFactors() {
        return extendingFactors;
    }

    /**
     * Получение {@link ApproxLoadParameters} по заданным параметрам. При построении происходит валидация.
     * В случае провала валидации выбрасывается IllegalStateException
     * @return параметры обучающей выборки
     */
    public ApproxLoadParameters build() {
        validate();
        prepare();
        return new ApproxLoadParameters(function, sizes, testSizes, validSizes, extendingFactors);
    }

    /**
     * Проверка заполненности полей. Обязательными являются - функция и размер обучающей выборки
     */
    private void validate() {
        if (function == null || (size < 1 && sizes == null))
            throw new IllegalStateException("Некорректное состояние билдера: " + this);
    }

    /**
     * Вычисление параметров, необходимых для построения {@link ApproxLoadParameters}
     */
    private void prepare() {
        testPart = testPart <= 0.0 ? defaultTestPart : testPart;
        testSize = testSize < 1 ? (int) (testPart * size) : testSize;
        
        validPart = validPart <= 0.0 ? defaultValidPart : validPart;
        validSize = validSize < 1 ? (int) (validPart * size) : validSize;
        
        extendingFactor = extendingFactor < 1.0 ? defaultExtendingFactor : extendingFactor;

        int inputsCount = function.getInputsCount();

        if (sizes == null)
            sizes = IntStream.range(0, inputsCount).map(i -> size).toArray();

        if (testSizes == null)
            testSizes = testParts == null ? 
                    IntStream.range(0, inputsCount).map(i -> testSize).toArray() :
                    IntStream.range(0, inputsCount).map(i -> (int) (testParts[i] * sizes[i])).toArray();

        if (validSizes == null)
            validSizes = validParts == null ? 
                    IntStream.range(0, inputsCount).map(i -> validSize).toArray() :
                    IntStream.range(0, inputsCount).map(i -> (int) (validParts[i] * sizes[i])).toArray();

        if (extendingFactors == null)
            extendingFactors = IntStream.range(0, inputsCount).mapToDouble(i -> extendingFactor).toArray();
    }

    @Override
    public String toString() {
        return "ApproxLoadParametersBuilder{" +
                "function=" + function +
                ", size=" + size +
                ", sizes=" + Arrays.toString(sizes) +
                ", testPart=" + testPart +
                ", testParts=" + Arrays.toString(testParts) +
                ", testSize=" + testSize +
                ", testSizes=" + Arrays.toString(testSizes) +
                ", validPart=" + validPart +
                ", validParts=" + Arrays.toString(validParts) +
                ", validSize=" + validSize +
                ", validSizes=" + Arrays.toString(validSizes) +
                ", extendingFactor=" + extendingFactor +
                ", extendingFactors=" + Arrays.toString(extendingFactors) +
                '}';
    }

    @Override
    public ApproxLoadParametersBuilder deepCopy() {
        return new ApproxLoadParametersBuilder()
                .function(function == null ? null : function.deepCopy())
                .size(size)
                .sizes(sizes)
                .testPart(testPart)
                .testParts(testParts)
                .testSize(testSize)
                .testSizes(testSizes)
                .validPart(validPart)
                .validParts(validParts)
                .validSize(validSize)
                .validSizes(validSizes)
                .extendingFactor(extendingFactor)
                .extendingFactors(extendingFactors);
    }
}
