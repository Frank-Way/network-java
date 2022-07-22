package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import utils.Utils;

public class BiasAdd extends ParametrizedOperation {
    public BiasAdd(@NotNull Matrix bias) {
        super(bias);
        if (bias.getCols() != 1)
            throw new IllegalArgumentException(String.format(
                    "Размерность матрицы (%d; %d) недопустима для смещения", bias.getRows(), bias.getCols()));
    }

    /***
     * copy-constructor
     */
    private BiasAdd(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient, Matrix parameter, Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient, parameter, parameterGradient);
    }

    @Override
    public BiasAdd copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        Matrix outputGradientCopy = Utils.copyNullable(outputGradient);
        Matrix inputGradientCopy = Utils.copyNullable(inputGradient);
        Matrix parameterCopy = Utils.copyNullable(parameter);
        Matrix parameterGradientCopy = Utils.copyNullable(parameterGradient);

        return new BiasAdd(inputCopy, outputCopy, outputGradientCopy, inputGradientCopy,
                parameterCopy, parameterGradientCopy);
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return input.addRow(parameter.transpose());
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return input.onesLike().mulCol(outputGradient);
    }

    @Override
    protected Matrix computeParameterGradient(@NotNull Matrix outputGradient) {
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
