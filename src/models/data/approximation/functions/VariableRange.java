package models.data.approximation.functions;

import models.math.Matrix;
import models.math.MatrixUtils;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Диапазон изменения входных переменных. Обе границы включаются в диапазон. Имеется возможность расширить границы на
 * заданный коэффициент. Атрибуты модели:
 *  left - левая граница диапазона;
 *  right - правая граница диапазона.
 */
@YamlSerializable
public class VariableRange implements DeepCopyable, Serializable {
    @YamlField private final double left;
    @YamlField private final double right;

    private VariableRange() {
        this(0, 0);
    }

    /**
     * Конструктор
     * @param left левая граница
     * @param right правая граница
     */
    public VariableRange(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    /**
     * Получение диапазона для заданных границ
     * @param size количество элементов
     * @return вектор-строка с диапазоном
     */
    public Matrix getRange(int size) {
        return MatrixUtils.getLinSpace(left, right, size);
    }

    /**
     * Получение расширенного диапазона
     * @param size количество элементов
     * @param extendingFactor коэффициент расширения
     * @return вектор-строка с диапазоном
     */
    public Matrix getExtendedRange(int size, double extendingFactor) {
        double mid = (right + left) / 2.0;
        double halfRange = (right - left) / 2.0 * extendingFactor;
        return MatrixUtils.getLinSpace(mid - halfRange, mid + halfRange, size);
    }

    @Override
    public VariableRange deepCopy() {
        return new VariableRange(left, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableRange that = (VariableRange) o;
        return Double.compare(that.left, left) == 0 && Double.compare(that.right, right) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "VariableRange{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
