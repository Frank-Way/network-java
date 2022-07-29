package models.operations.activations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.operations.Operation;
import utils.Utils;

/**
 * Линейная функция активации (без активации).
 * f(x) = x
 * f'(x) = 1
 */
public class Linear extends Operation {
    public Linear() {
        super();
    }

    /***
     * copy-constructor
     */
    private Linear(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return input;  // вход передаётся на выход без изменений
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return outputGradient;  // градиент с выхода передаётся на вход без изменений
    }

    @Override
    public Linear copy() {
        return new Linear(Utils.copyNullable(input), Utils.copyNullable(output),
                Utils.copyNullable(outputGradient), Utils.copyNullable(inputGradient));
    }

    @Override
    protected String getClassName() {
        return "ЛинейнаяФункцияАктивации";
    }

    @Override
    protected String getDebugClassName() {
        return "Linear";
    }
}
