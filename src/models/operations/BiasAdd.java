package models.operations;

import models.math.Matrix;

public class BiasAdd extends ParametrizedOperation {
    public BiasAdd(Matrix bias) {
        super(bias);
        if (bias.getCols() != 1)
            throw new IllegalArgumentException(String.format(
                    "Размерность матрицы (%d; %d) недопустима для смещения", bias.getRows(), bias.getCols()));
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input.addRow(parameter.transpose());
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return input.onesLike().mulCol(outputGradient);
    }

    @Override
    protected Matrix computeParameterGradient(Matrix outputGradient) {
        return outputGradient.sum(1).transpose();
    }

    @Override
    protected String getClassName() {
        return "Смещение";
    }

    @Override
    protected String getDebugClassName() {
        return "BiasAdd";
    }
}
