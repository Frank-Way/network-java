package models.operations.activations;

import models.math.Matrix;
import models.operations.Operation;

public class Linear extends Operation {
    public Linear() {
        super();
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input;
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return outputGradient;
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
