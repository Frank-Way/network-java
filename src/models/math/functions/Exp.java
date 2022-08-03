package models.math.functions;

/**
 * Применение экспоненты
 */
public class Exp implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.exp(value);
    }
}
