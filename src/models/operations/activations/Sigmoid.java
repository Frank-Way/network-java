package models.operations.activations;

import com.sun.istack.internal.NotNull;
import models.math.functions.Functions;
import models.math.Matrix;
import models.operations.Operation;
import utils.Utils;

public class Sigmoid extends Operation {
    public Sigmoid() {
        super();
    }

    /***
     * copy-constructor
     */
    private Sigmoid(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return input.onesLike().div(Functions.exp(input.mul(-1)).add(1));
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return output.mul(output.mul(-1).add(1)).mul(outputGradient);
    }

    @Override
    public Sigmoid copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        Matrix outputGradientCopy = Utils.copyNullable(outputGradient);
        Matrix inputGradientCopy = Utils.copyNullable(inputGradient);

        return new Sigmoid(inputCopy, outputCopy, outputGradientCopy, inputGradientCopy);
    }

    @Override
    protected String getClassName() {
        return "ЛогСигмоиднаяФункцияАктивации";
    }

    @Override
    protected String getDebugClassName() {
        return "Sigmoid";
    }
}
