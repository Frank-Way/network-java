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
     * @param inputs     количество входов слоя (нужно для определения формы матрицы весов)
     * @param neurons    размер слоя
     * @param activation функция активации (контроль за тем, действительно ли операция является функцией активации,
     *                   остаётся за пользователем)
     */
    public DenseLayer(int inputs, int neurons, Operation activation) {
        this(neurons, createOperations(inputs, neurons, activation));
    }

    /**
     * Конструктор
     * @param neurons    размер слоя
     * @param operations операции
     */
    public DenseLayer(int neurons, Operation[] operations) {
        this(null, null, neurons, operations);
    }

    /**
     * Конструктор для создания глубокой копии экземпляра
     */
    protected DenseLayer(Matrix input, Matrix output, int neurons, Operation[] operations) {
        super(input, output, neurons, operations);
    }

    /**
     * Конструктор для сериализации
     */
    private DenseLayer() {
        this(1, null);
    }

    /**
     * Создание операций
     * @param inputs     количество входов
     * @param neurons    количество нейронов
     * @param activation функция активации
     * @return           операции
     */
    protected static Operation[] createOperations(int inputs, int neurons, Operation activation) {
        /* для инициализации весов используется метод Ксавьера (Xavier или Glorot)

        метод заключается в том, что параметры инициализируются в соответствии с нормальным распределением, у
        которого математическое ожидание = 0, а СКО = 2 / (<количество входов> + <размер слоя>)

        применение метода позволяет не увеличивать МО входных значений при прохождении по сети */
        double scale = 2.0 / (inputs + neurons);

        // веса
        Matrix weight = MatrixUtils.getRandomMatrixNormal(inputs, neurons, 0, scale);

        // смещения
        Matrix bias = MatrixUtils.getRandomMatrixNormal(neurons, 1, 0, scale);

        return new Operation[]{
                new WeightMultiply(weight),
                new BiasAdd(bias),
                activation
        };
    }
}
