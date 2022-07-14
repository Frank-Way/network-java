package models.math.functions;

class Exp implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.exp(value);
    }
}
