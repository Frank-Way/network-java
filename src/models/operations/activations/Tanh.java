package models.operations.activations;

import models.math.functions.Functions;
import models.math.Matrix;
import models.operations.Operation;

public class Tanh  extends Operation {
    public Tanh() {
        super();
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return Functions.tanh(input);
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return outputGradient.mul(output.mul(output).mul(-1).add(1));
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