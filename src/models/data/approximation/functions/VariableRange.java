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
 * <pre><ul>
 *  <li>left  - левая граница диапазона;</li>
 *  <li>right - правая граница диапазона.</li>
 * </ul></pre>
 */
@YamlSerializable
public class VariableRange implements DeepCopyable, Serializable {
    @YamlField private final double left;
    @YamlField private final double right;

    /**
     * Конструктор, см. описание параметров в {@link VariableRange}
     */
    public VariableRange(double left, double right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Конструктор для сериализации
     */
    private VariableRange() {
        this(0, 0);
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
     * @return     вектор-стобец с диапазоном
     */
    public Matrix getRange(int size) {
        return MatrixUtils.getLinSpace(left, right, size);
    }

    /**
     * Получение расширенного диапазона
     * @param size            количество элементов
     * @param extendingFactor коэффициент расширения
     * @return                вектор-столбец с диапазоном
     */
    public Matrix getExtendedRange(int size, double extendingFactor) {
        final double mid = (right + left) / 2.0;  // середина диапазона
        final double halfRange = (right - left) / 2.0 * extendingFactor;  // половина длины расширенного диапазона
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
