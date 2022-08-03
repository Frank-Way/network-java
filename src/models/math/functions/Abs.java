package models.math.functions;

/**
 * Получение абсолютного значения
 */
public class Abs implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.abs(value);
    }
}
