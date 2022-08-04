package models.trainers;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.networks.Network;
import models.optimizers.Optimizer;
import utils.Utils;

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
 *  {@link Network.Builder} - билдер сетей с заданными настройками;
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
    private final Network.Builder networkBuilder;
    private final Optimizer.Builder optimizerBuilder;
    private final QueriesRangeType queriesRangeType;

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
     * @param networkBuilder  билдер сетей
     * @param optimizerBuilder  билдер оптимизатора
     * @param queriesRangeType  тип расчёта эпох для оценки сети
     */
    public FitParameters(@NotNull Dataset dataset,
                         int epochs,
                         int batchSize,
                         int queries,
                         boolean earlyStopping,
                         String doubleFormat,
                         boolean preTrainRequired,
                         int preTrainsCount,
                         double preTrainReduceFactor,
                         Network.Builder networkBuilder,
                         Optimizer.Builder optimizerBuilder,
                         QueriesRangeType queriesRangeType) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.doubleFormat = doubleFormat;
        this.preTrainRequired = preTrainRequired;
        this.preTrainsCount = preTrainsCount;
        this.preTrainReduceFactor = preTrainReduceFactor;
        this.networkBuilder = networkBuilder;
        this.optimizerBuilder = optimizerBuilder;
        this.queriesRangeType = queriesRangeType;
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

    public Network.Builder getNetworkBuilder() {
        return networkBuilder;
    }

    public Optimizer.Builder getOptimizerBuilder() {
        return optimizerBuilder;
    }

    public QueriesRangeType getQueriesRangeType() {
        return queriesRangeType;
    }

    /**
     * Получение копии {@link FitParameters} с уменьшенным количеством эпох для выполнения предобучения
     * @return  нужные параметры
     */
    public FitParameters preTrainCopy() {
        return new FitParameters(Utils.copyNullable(dataset),
                (int) (epochs / preTrainReduceFactor), batchSize, 1, false, doubleFormat,
                preTrainRequired, preTrainsCount, preTrainReduceFactor, Utils.copyNullable(networkBuilder),
                Utils.copyNullable(optimizerBuilder), queriesRangeType);
    }

    @Override
    public FitParameters copy() {
        return new FitParameters(Utils.copyNullable(dataset), epochs, batchSize, queries, earlyStopping,
                doubleFormat, preTrainRequired, preTrainsCount, preTrainReduceFactor, Utils.copyNullable(networkBuilder),
                Utils.copyNullable(optimizerBuilder), queriesRangeType);
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
                ", networkBuilder=" + networkBuilder +
                ", optimizerBuilder=" + optimizerBuilder +
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
