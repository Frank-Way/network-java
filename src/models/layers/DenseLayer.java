package models.layers;

import models.math.Matrix;
import models.math.MatrixOperations;
import models.operations.BiasAdd;
import models.operations.Operation;
import models.operations.WeightMultiply;

public class DenseLayer extends Layer{
    public DenseLayer(int inputs, int neurons, Operation activation) {
        super(neurons);
        double scale = 2.0 / (inputs + neurons);

        Matrix weight = MatrixOperations.getRandomMatrixNormal(inputs, neurons, 0, scale);
        addOperation(new WeightMultiply(weight));
        addParameter(weight);

        Matrix bias = MatrixOperations.getRandomMatrixNormal(neurons, 1, 0, scale);
        addOperation(new BiasAdd(bias));
        addParameter(bias);
        
        addOperation(activation);
    }

    @Override
    protected String getClassName() {
        return "ПолносвязныйСлой";
    }

    @Override
    protected String getDebugClassName() {
        return "DenseLayer";
    }
}
