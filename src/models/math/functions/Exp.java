package models.math.functions;

public class Exp implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.exp(value);
    }
}
