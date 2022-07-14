package models.math.functions;

public class Tanh implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.tanh(value);
    }
}
