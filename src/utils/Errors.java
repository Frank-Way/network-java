package utils;

import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.losses.MeanSquaredError;
import models.math.Matrix;

/**
 * Ошибки работы сети. Параметры модели:
 *  maxAbsoluteError - максимальная абсолютная ошибка, строится по всей обучающей выборке;
 *  maxRelativeError - максимальная относительная ошибка, равная отношению maxAbsoluteError к разнице между
 *                     максимальным и минимальным значением выборки;
 *  meanAbsoluteError - средняя абсолютная ошибка;
 *  lossMSE - средняя квадратическая ошибка.
 */
public class Errors implements Copyable<Errors>, Debuggable {
    private final double maxAbsoluteError;
    private final double maxRelativeError;
    private final double meanAbsoluteError;
    private final double lossMSE;

    /**
     * Конструктор
     * @param maxAbsoluteError  максимальная абсолютная ошибка
     * @param maxRelativeError  максимальная относительная ошибка
     * @param meanAbsoluteError  средняя абсолютная ошибка
     * @param lossMSE  средняя квадратическая ошибка
     */
    public Errors(double maxAbsoluteError, double maxRelativeError, double meanAbsoluteError, double lossMSE) {
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxRelativeError;
        this.meanAbsoluteError = meanAbsoluteError;
        this.lossMSE = lossMSE;
    }

    /**
     * Получение ошибок по целевым и вычисленным значениям
     * @param targets  требуемые теоретические выходы
     * @param predictions  выходы сети
     * @return  максимальные ошибки
     */
    public static Errors buildFromTargetsAndPredictions(Matrix targets, Matrix predictions) {
        Matrix errors = predictions.sub(targets).abs();
        double maxAbsoluteError = errors.max();
        MeanSquaredError mse = new MeanSquaredError();
        return new Errors(maxAbsoluteError,
                maxAbsoluteError / (targets.max() - targets.min()) * 100,
                errors.sum() / errors.size(),
                mse.forward(targets, predictions));
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
    public Errors copy() {
        return new Errors(maxAbsoluteError, maxRelativeError, meanAbsoluteError, lossMSE);
    }

    @Override
    public String toString() {
        return "Errors{" +
                "maxAbsoluteError=" + maxAbsoluteError +
                ", maxRelativeError=" + maxRelativeError +
                ", meanAbsoluteError=" + meanAbsoluteError +
                ", lossMSE=" + lossMSE +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "Ошибки{" +
                "максимальнаяАбсолютнаяОшибка=" + maxAbsoluteError +
                ", максимальнаяОтносительнаяОшибка=" + maxRelativeError +
                ", средняяАбсолютнаяОшибка=" + meanAbsoluteError +
                ", средняяКвадратическаяОшибка=" + lossMSE +
                '}';
    }
}
