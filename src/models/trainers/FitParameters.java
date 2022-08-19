package models.trainers;

import models.data.DataLoader;
import models.data.Dataset;
import models.data.LoadParameters;
import models.networks.NetworkBuilder;
import models.optimizers.Optimizer;
import models.optimizers.OptimizerBuilder;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.CopyUtils;
import utils.copy.DeepCopyable;

/**
 * Параметры обучения для метода fit класса {@link Trainer}. Атрибуты модели:
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
@YamlSerializable
public class FitParameters implements DeepCopyable {
    private Dataset dataset;
    @YamlField private final DataLoader dataLoader;
    @YamlField private final LoadParameters loadParameters;
    @YamlField private final int epochs;
    @YamlField private final int batchSize;
    @YamlField private final int queries;
    @YamlField private final boolean earlyStopping;
    @YamlField private final String doubleFormat;
    @YamlField private final boolean preTrainRequired;
    @YamlField private final int preTrainsCount;
    @YamlField private final double preTrainReduceFactor;
    @YamlField private final NetworkBuilder networkBuilder;
    @YamlField private final OptimizerBuilder optimizerBuilder;
    @YamlField private final QueriesRangeType queriesRangeType;

    /**
     * Конструктор
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
    public FitParameters(DataLoader dataLoader,
                         LoadParameters loadParameters,
                         int epochs,
                         int batchSize,
                         int queries,
                         boolean earlyStopping,
                         String doubleFormat,
                         boolean preTrainRequired,
                         int preTrainsCount,
                         double preTrainReduceFactor,
                         NetworkBuilder networkBuilder,
                         OptimizerBuilder optimizerBuilder,
                         QueriesRangeType queriesRangeType) {
        this.dataLoader = dataLoader;
        this.loadParameters = loadParameters;
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

    private FitParameters() {
        this(null,
                null,
                0,
                0,
                0,
                false,
                null,
                false,
                0,
                0,
                null,
                null,
                null);
    }

    private FitParameters(Dataset dataset,
                         DataLoader dataLoader,
                         LoadParameters loadParameters,
                         int epochs,
                         int batchSize,
                         int queries,
                         boolean earlyStopping,
                         String doubleFormat,
                         boolean preTrainRequired,
                         int preTrainsCount,
                         double preTrainReduceFactor,
                         NetworkBuilder networkBuilder,
                         OptimizerBuilder optimizerBuilder,
                         QueriesRangeType queriesRangeType) {
        this.dataset = dataset;
        this.dataLoader = dataLoader;
        this.loadParameters = loadParameters;
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

    public Dataset loadDataset() {
        dataset = dataLoader.load(loadParameters);
        return dataset;
    }

    public Dataset getDataset() {
        return dataset == null ? loadDataset() : dataset;
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public LoadParameters getLoadParameters() {
        return loadParameters;
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

    public NetworkBuilder getNetworkBuilder() {
        return networkBuilder;
    }

    public OptimizerBuilder getOptimizerBuilder() {
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
        return new FitParameters(dataset == null ? null : dataset.deepCopy(), dataLoader == null ? null : dataLoader.deepCopy(),
                loadParameters == null ? null : loadParameters.deepCopy(), (int) (epochs / preTrainReduceFactor), batchSize, 1,
                false, doubleFormat, preTrainRequired, preTrainsCount, preTrainReduceFactor,
                networkBuilder.deepCopy(), optimizerBuilder.deepCopy(), queriesRangeType);
    }

    @Override
    public FitParameters deepCopy() {
        return new FitParameters(dataset == null ? null : dataset.deepCopy(), dataLoader == null ? null : dataLoader.deepCopy(),
                loadParameters == null ? null : loadParameters.deepCopy(), epochs, batchSize, queries, earlyStopping,
                doubleFormat, preTrainRequired, preTrainsCount, preTrainReduceFactor, networkBuilder.deepCopy(),
                optimizerBuilder.deepCopy(), queriesRangeType);
    }

    @Override
    public String toString() {
        return "FitParameters{" +
                "dataLoader=" + dataLoader +
                ", loadParameters=" + loadParameters +
                ", epochs=" + epochs +
                ", batchSize=" + batchSize +
                ", queries=" + queries +
                ", earlyStopping=" + earlyStopping +
                ", doubleFormat='" + doubleFormat + '\'' +
                ", preTrainRequired=" + preTrainRequired +
                ", preTrainsCount=" + preTrainsCount +
                ", preTrainReduceFactor=" + preTrainReduceFactor +
                ", networkBuilder=" + networkBuilder +
                ", optimizerBuilder=" + optimizerBuilder +
                ", queriesRangeType=" + queriesRangeType +
                '}';
    }
}
