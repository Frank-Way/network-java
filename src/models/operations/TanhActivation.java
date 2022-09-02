package models.operations;

import models.math.Matrix;
import models.math.functions.MatrixFunctions;

/**
 * Гиперболический тангенс. Может быть выражен через сигмоидную функцию активаации:
 * <pre>
 * f(x) = tanh(x) = = 2 * sigmoid(2 * x) - 1 = 2 / (1 + exp(-2 * x)) - 1
 * f'(x) = f(x) * (1 - f(x))
 * </pre>
 */
public class TanhActivation extends Operation {
    /**
     * Конструктор
     */
    public TanhActivation() {
        super();
    }

    /**
     * Конструктор для создания глубокой копии экземпляра
     */
    protected TanhActivation(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return MatrixFunctions.tanh(input);  // применение функции к каждому элементу
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        /*
        [1]: output = f(x)
        [2]: [1].mul[1] = f^2(x) = f(x) * f(x)
        [3]: [2].mul(-1) = -f^2(x) = -(f(x) * f(x))
        [4]: [3].add(1) = 1 - f^2(x) = 1 - f(x) * f(x)
        [5]: outputGradient.mul([4]) - домножение на градиент по правилу цепочки
         */
        return outputGradient.mul(output.mul(output).mul(-1).add(1));
    }
}