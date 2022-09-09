package utils;

import models.losses.MeanSquaredError;
import models.math.Matrix;
import utils.copy.DeepCopyable;

/**
 * Ошибки работы сети. Параметры модели:
 *  maxAbsoluteError - максимальная абсолютная ошибка, строится по всей обучающей выборке;
 *  maxRelativeError - максимальная относительная ошибка, равная отношению maxAbsoluteError к разнице между
 *                     максимальным и минимальным значением выборки;
 *  maxRelativeError2 - максимальная относительная ошибка2, равная максимальному отношению ошибки к требуемому значению;
 *  meanAbsoluteError - средняя абсолютная ошибка;
 *  lossMSE - средняя квадратическая ошибка.
 */
public class Errors implements DeepCopyable {
    private final double maxAbsoluteError;
    private final double maxRelativeError;
    private final double maxRelativeError2;
    private final double meanAbsoluteError;
    private final double lossMSE;

    /**
     * Конструктор
     * @param maxAbsoluteError  максимальная абсолютная ошибка
     * @param maxRelativeError  максимальная относительная ошибка
     * @param maxRelativeError2 максимальная относительная ошибка2
     * @param meanAbsoluteError средняя абсолютная ошибка
     * @param lossMSE           средняя квадратическая ошибка
     */
    public Errors(double maxAbsoluteError, double maxRelativeError, double maxRelativeError2, double meanAbsoluteError, double lossMSE) {
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxRelativeError;
        this.maxRelativeError2 = maxRelativeError2;
        this.meanAbsoluteError = meanAbsoluteError;
        this.lossMSE = lossMSE;
    }

    /**
     * Конструктор. Получение ошибок по целевым и вычисленным значениям
     * @param targets     требуемые теоретические выходы
     * @param predictions выходы сети
     */
    public Errors(Matrix targets, Matrix predictions) {
        Matrix errors = predictions.sub(targets).abs();
        double maxAbsoluteError = errors.max();
        MeanSquaredError mse = new MeanSquaredError();
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxAbsoluteError / (targets.max() - targets.min()) * 100;
        Matrix absTargets = targets.abs();
        double maxRelativeError2 = Double.MIN_VALUE;
        for (int row = 0; row < absTargets.getRows(); row++)
            for (int col = 0; col < absTargets.getCols(); col++) {
                double targetValue = absTargets.getValue(row, col);
                if (targetValue > 0)
                    maxRelativeError2 = Math.max(maxRelativeError2, errors.getValue(row, col) / targetValue);
            }
        this.maxRelativeError2 = maxRelativeError2 * 100;
        this.meanAbsoluteError = errors.sum() / errors.size();
        this.lossMSE = mse.forward(targets, predictions);
    }

    public double getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    public double getMaxRelativeError() {
        return maxRelativeError;
    }

    public double getMeanAbsoluteError() {
        return meanAbsoluteError;
    }

    public double getLossMSE() {
        return lossMSE;
    }

    @Override
    public Errors deepCopy() {
        return new Errors(maxAbsoluteError, maxRelativeError, maxRelativeError2, meanAbsoluteError, lossMSE);
    }

    @Override
    public String toString() {
        return "Errors{" +
                "maxAbsoluteError=" + maxAbsoluteError +
                ", maxRelativeError=" + maxRelativeError +
                ", maxRelativeError2=" + maxRelativeError2 +
                ", meanAbsoluteError=" + meanAbsoluteError +
                ", lossMSE=" + lossMSE +
                '}';
    }
}
