package models.trainers;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.networks.NetworkBuilder;
import models.networks.NetworkBuilderParameters;
import models.optimizers.Optimizer;
import utils.Utils;

import java.util.Objects;

/**
 * Параметры обучения для метода fit класса {@link Trainer}. Атрибуты модели:
 *  {@link Dataset} - обучающая выборка;
 *  epochs - количество эпох обучения;
 *  batchSize - размер пакета, на которые разбивается выборка при обучении;
 *  queries - количество опросов/оценок (вычисление потери) при обучении;
 *  earlyStopping - остановить обучение при не-уменьшении потерь во время обучения;
 *  doubleFormat - формат вывода вещественных чисел;
 *  preTrainRequired - нужно ли выполнять предобучение;
 *  preTrainCount - количество попыток предобучения;
 *  preTrainReduceFactor - во сколько раз снижается количество эпох при предобучении в сравнении с epochs;
 *  {@link NetworkBuilder} - билдер сетей с заданными настройками;
 *  {@link Optimizer} - оптимизатор сети
 */
public class FitParameters implements Copyable<FitParameters>, Debuggable {
    private final Dataset dataset;
    private final int epochs;
    private final int batchSize;
    private final int queries;
    private final boolean earlyStopping;
    private final String doubleFormat;
    private final boolean preTrainRequired;
    private final int preTrainsCount;
    private final double preTrainReduceFactor;
    private final NetworkBuilderParameters networkBuilderParameters;
    private final Optimizer optimizer;

    /**
     * Конструктор
     * @param dataset  обучающая выборка
     * @param epochs  количество эпох обучения
     * @param batchSize  размер пакета
     * @param queries  количество опросов
     * @param earlyStopping  остановить обучение
     * @param doubleFormat  формат вывода вещественных чисел
     * @param preTrainRequired  нужно ли предобучение
     * @param preTrainsCount  количество попыток предобучения
     * @param preTrainReduceFactor  во сколько раз снижается количество эпох при предобучении
     * @param networkBuilderParameters  билдер сетей
     * @param optimizer  оптимизатор
     */
    public FitParameters(@NotNull Dataset dataset, int epochs, int batchSize, int queries,
                         boolean earlyStopping, String doubleFormat, boolean preTrainRequired,
                         int preTrainsCount, double preTrainReduceFactor, NetworkBuilderParameters networkBuilderParameters,
                         Optimizer optimizer) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.doubleFormat = doubleFormat;
        this.preTrainRequired = preTrainRequired;
        this.preTrainsCount = preTrainsCount;
        this.preTrainReduceFactor = preTrainReduceFactor;
        this.networkBuilderParameters = networkBuilderParameters;
        this.optimizer = optimizer;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public int getEpochs() {
        return epochs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getQueries() {
        return queries;
    }

    public boolean isEarlyStopping() {
        return earlyStopping;
    }

    public String getDoubleFormat() {
        return doubleFormat;
    }

    public boolean isPreTrainRequired() {
        return preTrainRequired;
    }

    public int getPreTrainsCount() {
        return preTrainsCount;
    }

    public double getPreTrainReduceFactor() {
        return preTrainReduceFactor;
    }

    public NetworkBuilderParameters getNetworkBuilderParameters() {
        return networkBuilderParameters;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    /**
     * Получение копии {@link FitParameters} с уменьшенным количеством эпох для выполнения предобучения
     * @return  нужные параметры
     */
    public FitParameters preTrainCopy() {
        return new FitParameters(Utils.copyNullable(dataset),
                (int) (epochs / preTrainReduceFactor), batchSize, 1, false, doubleFormat,
                preTrainRequired, preTrainsCount, preTrainReduceFactor, Utils.copyNullable(networkBuilderParameters),
                Utils.copyNullable(optimizer));
    }

    @Override
    public FitParameters copy() {
        return new FitParameters(Utils.copyNullable(dataset), epochs, batchSize, queries, earlyStopping,
                doubleFormat, preTrainRequired, preTrainsCount, preTrainReduceFactor, Utils.copyNullable(networkBuilderParameters),
                Utils.copyNullable(optimizer));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FitParameters that = (FitParameters) o;
        return epochs == that.epochs &&
               batchSize == that.batchSize &&
               queries == that.queries &&
               preTrainRequired == that.preTrainRequired &&
               earlyStopping == that.earlyStopping &&
               preTrainsCount == that.preTrainsCount &&
               preTrainReduceFactor == that.preTrainReduceFactor &&
               Objects.equals(dataset, that.dataset) &&
               Objects.equals(networkBuilderParameters, that.networkBuilderParameters) &&
               Objects.equals(optimizer, that.optimizer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataset, epochs, batchSize, queries, earlyStopping,
                preTrainRequired, preTrainsCount, preTrainReduceFactor, networkBuilderParameters, optimizer);
    }

    @Override
    public String toString() {
        return "FitParameters{" +
                "dataset=" + dataset +
                ", epochs=" + epochs +
                ", batchSize=" + batchSize +
                ", queries=" + queries +
                ", earlyStopping=" + earlyStopping +
                ", preTrainRequired=" + preTrainRequired +
                ", preTrainsCount=" + preTrainsCount +
                ", preTrainReduceFactor=" + preTrainReduceFactor +
                ", networkBuilderParameters=" + networkBuilderParameters +
                ", optimizer=" + optimizer +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "ПараметрыОбучения{" +
                "размерВыборкиОбучения=" + dataset.getTrainData().getRows() +
                ", эпох=" + epochs +
                '}';
    }
}
