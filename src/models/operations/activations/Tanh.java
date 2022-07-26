package models.operations.activations;

import com.sun.istack.internal.NotNull;
import models.math.MatrixOperations;
import models.math.Matrix;
import models.operations.Operation;
import utils.Utils;

public class Tanh  extends Operation {
    public Tanh() {
        super();
    }

    /***
     * copy-constructor
     */
    private Tanh(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return MatrixOperations.Functions.tanh(input);
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return outputGradient.mul(output.mul(output).mul(-1).add(1));
    }

    @Override
    public Tanh copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        Matrix outputGradientCopy = Utils.copyNullable(outputGradient);
        Matrix inputGradientCopy = Utils.copyNullable(inputGradient);

        return new Tanh(inputCopy, outputCopy, outputGradientCopy, inputGradientCopy);
    }

    @Override
    protected String getClassName() {
        return "ФункцияАктивацииГиперболическийТангенс";
    }

    @Override
    protected String getDebugClassName() {
        return "Tanh";
    }
}