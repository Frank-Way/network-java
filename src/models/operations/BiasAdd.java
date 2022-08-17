package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import utils.Utils;

import java.util.Arrays;

/**
 * Добавление смещения
 */
public class BiasAdd extends ParametrizedOperation {

    public BiasAdd(@NotNull Matrix bias) {
        super(bias);
        if (!bias.isCol())
            throw new IllegalArgumentException(String.format(
                    "Размерность матрицы (%d; %d) недопустима для смещения", bias.getRows(), bias.getCols()));
    }

    /**
     * copy-constructor
     */
    private BiasAdd(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient, Matrix parameter, Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient, parameter, parameterGradient);
    }

    @Override
    public BiasAdd copy() {
        return new BiasAdd(Utils.copyNullable(input), Utils.copyNullable(output), Utils.copyNullable(outputGradient),
                Utils.copyNullable(inputGradient), Utils.copyNullable(parameter), Utils.copyNullable(parameterGradient));
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return input.addRow(parameter.transpose());  // добавление смещений как строки
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return input.onesLike().mulCol(outputGradient);  // суммирование градиента определенным образом
    }

    @Override
    protected Matrix computeParameterGradient(@NotNull Matrix outputGradient) {
        return outputGradient.sum(1).transpose();  // суммирование градиента по строкам
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
