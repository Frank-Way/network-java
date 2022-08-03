package models.math.functions;

/**
 * Возведение в степень
 */
public class Pow implements DoubleOperation {
    double scale;  // степень

    public Pow(double scale) {
        this.scale = scale;
    }

    @Override
    public double apply(double value) {
        return Math.pow(value, scale);
    }
}
