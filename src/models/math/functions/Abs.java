package models.math.functions;

class Abs implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.abs(value);
    }
}
