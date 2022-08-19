package models.operations;

import models.math.Matrix;

/**
 * Линейная функция активации (без активации).
 * f(x) = x
 * f'(x) = 1
 */
public class LinearActivation extends Operation {
    public LinearActivation() {
        super();
    }

    /***
     * copy-constructor
     */
    protected LinearActivation(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input;  // вход передаётся на выход без изменений
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return outputGradient;  // градиент с выхода передаётся на вход без изменений
    }
}
