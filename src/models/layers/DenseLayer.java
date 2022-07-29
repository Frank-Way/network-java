package models.layers;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.operations.BiasAdd;
import models.operations.Operation;
import models.operations.WeightMultiply;
import utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class DenseLayer extends Layer{
    public DenseLayer(int inputs, int neurons, @NotNull Operation activation) {
        super(neurons);
        double scale = 2.0 / (inputs + neurons);

        Matrix weight = MatrixOperations.getRandomMatrixNormal(inputs, neurons, 0, scale);
        addOperation(new WeightMultiply(weight));

        Matrix bias = MatrixOperations.getRandomMatrixNormal(neurons, 1, 0, scale);
        addOperation(new BiasAdd(bias));

        addOperation(activation);
    }

    /***
     * copy-constructor
     */
    private DenseLayer(Matrix input,
                      Matrix output,
                      int neurons,
                      List<Operation> operations) {
        super(input, output, neurons, operations);
    }

    @Override
    public DenseLayer copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        List<Operation> operationsCopy = operations.stream().map(Utils::copyNullable).collect(Collectors.toList());

        return new DenseLayer(inputCopy, outputCopy, neurons, operationsCopy);
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
