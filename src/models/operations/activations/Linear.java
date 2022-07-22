package models.operations.activations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.operations.Operation;
import utils.Utils;

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
        return input;
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return outputGradient;
    }

    @Override
    public Linear copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        Matrix outputGradientCopy = Utils.copyNullable(outputGradient);
        Matrix inputGradientCopy = Utils.copyNullable(inputGradient);

        return new Linear(inputCopy, outputCopy, outputGradientCopy, inputGradientCopy);
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
