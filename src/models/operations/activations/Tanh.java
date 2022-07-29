package models.operations.activations;

import com.sun.istack.internal.NotNull;
import models.math.MatrixOperations;
import models.math.Matrix;
import models.operations.Operation;
import utils.Utils;

/**
 * Гиперболический тангенс. Может быть выражен через сигмоидную функцию активаации:
 * f(x) = tanh(x) = = 2 * sigmoid(2 * x) - 1 = 2 / (1 + exp(-2 * x)) - 1
 * f'(x) = f(x) * (1 - f(x))
 */
public class Tanh  extends Operation {
    public Tanh() {
        super();
    }

    /**
     * copy-constructor
     */
    private Tanh(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        super(input, output, outputGradient, inputGradient);
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return MatrixOperations.Functions.tanh(input);  // применение функции к каждому элементу
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        /*
        [1]: output = f(x)
        [2]: [1].mul[1] = f^2(x) = f(x) * f(x)
        [3]: [2].mul(-1) = -f^2(x) = -(f(x) * f(x))
        [4]: [3].add(1) = 1 - f^2(x) = 1 - f(x) * f(x)
        [5]: outputGradient.mul([4]) - домножение на градиент по правилу цепочки
         */
        return outputGradient.mul(output.mul(output).mul(-1).add(1));
    }

    @Override
    public Tanh copy() {
        return new Tanh(Utils.copyNullable(input), Utils.copyNullable(output),
                Utils.copyNullable(outputGradient), Utils.copyNullable(inputGradient));
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