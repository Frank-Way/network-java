package models.math.functions;

public class Pow implements DoubleOperation {
    double scale;

    public Pow(double scale) {
        this.scale = scale;
    }

    @Override
    public double apply(double value) {
        return Math.pow(value, scale);
    }
}
