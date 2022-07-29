package models.operations.activations;

import com.sun.istack.internal.NotNull;
import models.math.MatrixOperations;
import models.math.Matrix;
import models.operations.Operation;
import utils.Utils;

/**
 * Логистическая (сигмоидная, лог-сигмоидная) функция активации.
 * f(x) = 1 / (1 + exp(-x))
 * f'(x) = f(x) * (1 - f(x))
 */
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
        /*
        [1]: input.mul(-1) = -x
        [2]: MatrixOperations.Functions.exp([1]) = exp(-x)
        [3]: [2].add(1) = 1 + exp(-x)
        [4]: input.onesLike() = 1 (матрица)
        [5]: [4].div([3]) = 1 / (1 + exp(-x))
         */
        return input.onesLike().div(MatrixOperations.Functions.exp(input.mul(-1)).add(1));
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        /*
        [1]: output = f(x)
        [2]: [1].mul(-1) = -f(x)
        [3]: [2].add(1) = 1 - f(x)
        [4]: [1].mul([3]) = f(x) * (1 - f(x))
        [5]: [4].mul(outputGradient) - домножение на градиент по правилу цепочки
         */
        return output.mul(output.mul(-1).add(1)).mul(outputGradient);
    }

    @Override
    public Sigmoid copy() {
        return new Sigmoid(Utils.copyNullable(input), Utils.copyNullable(output),
                Utils.copyNullable(outputGradient), Utils.copyNullable(inputGradient));
    }

    @Override
    protected String getClassName() {
        return "СигмоиднаяФункцияАктивации";
    }

    @Override
    protected String getDebugClassName() {
        return "Sigmoid";
    }
}
