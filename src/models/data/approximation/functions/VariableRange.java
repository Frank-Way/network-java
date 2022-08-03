package models.data.approximation.functions;

import models.math.Matrix;
import models.math.MatrixOperations;

/**
 * Диапазон изменения входных переменных. Обе границы включаются в диапазон. Имеется возможность расширить границы на
 * заданный коэффициент. Атрибуты модели:
 *  left - левая граница диапазона;
 *  right - правая граница диапазона.
 */
public class VariableRange {
    private final double left;
    private final double right;

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
        return MatrixOperations.getLinSpace(left, right, size);
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
        return MatrixOperations.getLinSpace(mid - halfRange, mid + halfRange, size);
    }
}
