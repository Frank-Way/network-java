package models.data.approximation.functions;

import models.math.Matrix;
import models.math.MatrixOperations;

public class VariableRange {
    private final double left;
    private final double right;

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

    public Matrix getRange(int size) {
        return MatrixOperations.getLinSpace(left, right, size);
    }

    public Matrix getExtendedRange(int size, double extendingFactor) {
        double mid = (right + left) / 2.0;
        double halfRange = (right - left) / 2.0 * extendingFactor;
        return MatrixOperations.getLinSpace(mid - halfRange, mid + halfRange, size);
    }
}
