package models.math.functions;

/**
 * Применение гиперболического тангенса
 */
public class Tanh implements DoubleOperation {
    @Override
    public double apply(double value) {
        return Math.tanh(value);
    }
}
