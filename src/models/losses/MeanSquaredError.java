package models.losses;

import models.math.Matrix;

/**
 * Среднеквадратическая ошибка.
 * <pre><ul>
 * <li>f(Y, T) = 1 / N * сумма[(Y - T) ^ 2], где Y - выходы сети, T - требуемые выходы сети, N - количество строк в выборке;</li>
 * <li>f(Y, T) - скаляр.</li>
 * <li>d/dY f(Y, T) = 2 * (Y - T) / N.</li>
 * <li>d/dY f(Y, T) - матрица.</li>
 * </ul></pre>
 */
public class MeanSquaredError extends Loss{
    /**
     * Конструктор
     */
    public MeanSquaredError() {super();}

    /***
     * Конструктор для создания глубокой копии экземпляра
     */
    protected MeanSquaredError(Matrix prediction, Matrix target, double output, Matrix inputGradient) {
        super(prediction, target, output, inputGradient);
    }

    @Override
    protected double computeOutput(Matrix prediction, Matrix target) {
        /*
        [1]: prediction = Y
        [2]: target = T
        [3]: prediction.getRows() = N
        [4]: [1].sub([2]) = Y - T
        [5]: [4].mul([4]) = (Y - T) ^ 2
        [6]: [5].sum() = сумма[(Y - T) ^ 2]
        [7]: [6] / [3] = 1 / N * сумма[(Y - T) ^ 2]
         */
        return prediction.sub(target).mul(prediction.sub(target)).sum() / prediction.getRows();
    }

    @Override
    protected Matrix computeInputGradient(Matrix prediction, Matrix target) {
        /*
        [1]: prediction = Y
        [2]: target = T
        [3]: prediction.getRows() = N
        [4]: [1].sub([2]) = Y - T
        [5]: [4].mul(2) = 2 * (Y - T)
        [6]: [5].div([3]) = 2 * (Y - T) / N
         */
        return prediction.sub(target).mul(2).div(prediction.getRows());
    }

    @Override
    public MeanSquaredError deepCopy() {
        return new MeanSquaredError();
    }
}
