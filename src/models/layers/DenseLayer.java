package models.layers;

import models.math.Matrix;
import models.math.MatrixUtils;
import models.operations.BiasAdd;
import models.operations.Operation;
import models.operations.WeightMultiply;

/**
 * Полносвязный слой
 */
public class DenseLayer extends Layer{
    /**
     * Конструктор. При создании инициализируются параметры (веса и смещения)
     * @param inputs  количество входов слоя (нужно для определения формы матрицы весов)
     * @param neurons размер слоя
     * @param activation функция активации (контроль за тем, действительно ли операция является функцией активации,
     *                   остаётся за пользователем)
     */
    public DenseLayer(int inputs, int neurons, Operation activation) {
        this(neurons, createOperations(inputs, neurons, activation));
    }

    private DenseLayer() {
        this(1, null);
    }

    public DenseLayer(int neurons, Operation[] operations) {
        this(null, null, neurons, operations);
    }

    protected DenseLayer(Matrix input, Matrix output, int neurons, Operation[] operations) {
        super(input, output, neurons, operations);
    }

    protected static Operation[] createOperations(int inputs, int neurons, Operation activation) {
        Operation[] result = new Operation[3];
        /* для инициализации весов используется метод Ксавьера (Xavier или Glorot)

        метод заключается в том, что параметры инициализируются в соответствии с нормальным распределением, у
        которого математическое ожидание = 0, а СКО = 2 / (<количество входов> + <размер слоя>)

        применение метода позволяет не увеличивать МО входных значений при прохождении по сети */
        double scale = 2.0 / (inputs + neurons);

        Matrix weight = MatrixUtils.getRandomMatrixNormal(inputs, neurons, 0, scale);
        result[0] = new WeightMultiply(weight);

        Matrix bias = MatrixUtils.getRandomMatrixNormal(neurons, 1, 0, scale);
        result[1] = new BiasAdd(bias);

        result[2] = activation;
        return result;
    }
}
