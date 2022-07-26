package models.math.functions;

public class Abs implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.abs(value);
    }
}
