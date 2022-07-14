package models.operations.activations;

import models.math.functions.Functions;
import models.math.Matrix;
import models.operations.Operation;

public class Sigmoid extends Operation {
    public Sigmoid() {
        super();
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input.onesLike().div(Functions.exp(input.mul(-1)).add(1));
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return output.mul(output.mul(-1).add(1)).mul(outputGradient);
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
