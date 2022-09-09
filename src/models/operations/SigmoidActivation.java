package models.operations;

import models.math.Matrix;
import models.math.functions.MatrixFunctions;

/**
 * Логистическая (сигмоидная, лог-сигмоидная) функция активации.
 * <pre>
 * f(x) = 1 / (1 + exp(-x))
 * f'(x) = f(x) * (1 - f(x))
 * </pre>
 */
public class SigmoidActivation extends Operation {
    /**
     * Конструктор
     */
    public SigmoidActivation() {
        super();
    }

    /***
     * Конструктор для создания глубокой копии экземпляра
     */
    protected SigmoidActivation(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        /*
        [1]: input.mul(-1) = -x
        [2]: MatrixOperations.Functions.exp([1]) = exp(-x)
        [3]: [2].add(1) = 1 + exp(-x)
        [4]: input.onesLike() = 1 (матрица)
        [5]: [4].div([3]) = 1 / (1 + exp(-x))
         */
        return input.onesLike().div(MatrixFunctions.exp(input.mul(-1)).add(1));
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        /*
        [1]: output = f(x)
        [2]: [1].mul(-1) = -f(x)
        [3]: [2].add(1) = 1 - f(x)
        [4]: [1].mul([3]) = f(x) * (1 - f(x))
        [5]: [4].mul(outputGradient) - домножение на градиент по правилу цепочки
         */
        return output.mul(output.mul(-1).add(1)).mul(outputGradient);
    }
}
