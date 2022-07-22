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
        addParameter(weight);

        Matrix bias = MatrixOperations.getRandomMatrixNormal(neurons, 1, 0, scale);
        addOperation(new BiasAdd(bias));
        addParameter(bias);
        
        addOperation(activation);
    }

    /***
     * copy-constructor
     */
    private DenseLayer(Matrix input,
                      Matrix output,
                      int neurons,
                      List<Matrix> parameters,
                      List<Matrix> parameterGradients,
                      List<Operation> operations) {
        super(input, output, neurons, parameters, parameterGradients, operations);
    }

    @Override
    public DenseLayer copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        List<Matrix> parametersCopy = parameters.stream().map(Utils::copyNullable).collect(Collectors.toList());
        List<Matrix> parameterGradientsCopy = parameterGradients.stream().map(Utils::copyNullable).collect(Collectors.toList());
        List<Operation> operationsCopy = operations.stream().map(Utils::copyNullable).collect(Collectors.toList());

        return new DenseLayer(inputCopy, outputCopy, neurons, parametersCopy, parameterGradientsCopy, operationsCopy);
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
