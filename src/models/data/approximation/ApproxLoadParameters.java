package models.data.approximation;

import models.data.LoadParameters;
import models.data.approximation.functions.Function;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Параметры метода load класса {@link ApproxDataLoader}. Атрибуты модели:
 * <pre><ul>
 *  <li>{@link Function} - функция, на основе которой будет построена обучающая выборка;</li>
 *  <li>sizes            - размеры выборки;</li>
 *  <li>testSizes        - размеры тестовой выборки;</li>
 *  <li>validSizes       - размеры валидационной выборки;</li>
 *  <li>extendingFactors - коэффициенты расширения диапазонов входных переменных</li>
 *  <li>noiseMode        - тип зашумления выборки</li>
 * </ul></pre>
 */
@YamlSerializable
public class ApproxLoadParameters extends LoadParameters implements DeepCopyable, Serializable {
    @YamlField private final Function function;
    @YamlField private final int[] sizes;
    @YamlField private final int[] testSizes;
    @YamlField private final int[] validSizes;
    @YamlField private final double[] extendingFactors;

    /**
     * Конструктор, см. описание параметров в {@link ApproxLoadParameters}
     */
    public ApproxLoadParameters(Function function, int[] sizes, int[] testSizes,
                                int[] validSizes, double[] extendingFactors, NoiseMode noiseMode) {
        super(noiseMode);
        this.function = function;
        this.sizes = sizes;
        this.testSizes = testSizes;
        this.validSizes = validSizes;
        this.extendingFactors = extendingFactors;
    }

    /**
     * Конструктор для сериализации
     */
    private ApproxLoadParameters() {
        this(null,
                null,
                null,
                null,
                null,
                null);
    }

    public Function getFunction() {
        return function;
    }

    public int[] getSizes() {
        return sizes;
    }

    public int[] getTestSizes() {
        return testSizes;
    }

    public int[] getValidSizes() {
        return validSizes;
    }

    public double[] getExtendingFactors() {
        return extendingFactors;
    }


    /**
     * Получение пустого билдера
     * @return новый билдер
     */
    public static ApproxLoadParametersBuilder newBuilder() {
        return new ApproxLoadParametersBuilder();
    }

    /**
     * Получение билдера на основе текущего экземпляра
     * @return билдер с заполненными полями
     */
    public ApproxLoadParametersBuilder builder() {
        return newBuilder()
                .function(function.deepCopy())
                .sizes(Arrays.copyOf(sizes, sizes.length))
                .testSizes(Arrays.copyOf(testSizes, testSizes.length))
                .validSizes(Arrays.copyOf(validSizes, validSizes.length))
                .extendingFactors(Arrays.copyOf(extendingFactors, extendingFactors.length));
    }

    @Override
    public String toString() {
        return "ApproxLoadParameters{" +
                "function=" + function +
                ", sizes=" + Arrays.toString(sizes) +
                ", testSizes=" + Arrays.toString(testSizes) +
                ", validSizes=" + Arrays.toString(validSizes) +
                ", extendingFactors=" + Arrays.toString(extendingFactors) +
                '}';
    }

    @Override
    public ApproxLoadParameters deepCopy() {
        return builder().build();
    }
}
