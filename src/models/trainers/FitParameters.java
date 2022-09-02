package models.trainers;

import models.data.Dataset;
import models.networks.NetworkBuilder;
import models.optimizers.Optimizer;
import models.optimizers.OptimizerBuilder;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

/**
 * Параметры обучения для метода fit класса {@link Trainer}. Атрибуты модели:
 * <pre><ul>
 *  <li>{@link Dataset}          - обучающая выборка;</li>
 *  <li>epochs                   - количество эпох обучения;</li>
 *  <li>batchSize                - размер пакета, на которые разбивается выборка при обучении;</li>
 *  <li>queries                  - количество опросов/оценок (вычисление потери) при обучении;</li>
 *  <li>earlyStopping            - остановить обучение при не-уменьшении потерь во время обучения;</li>
 *  <li>earlyStoppingThreshold   - порог срабатывания ранней установки (сколько опросов должно давать худший результат,
 *                                 в сравнении с предыдущим, чтобы обучение остановилось);</li>
 *  <li>doubleFormat             - формат вывода вещественных чисел;</li>
 *  <li>preTrainRequired         - нужно ли выполнять предобучение;</li>
 *  <li>preTrainCount            - количество попыток предобучения;</li>
 *  <li>preTrainReduceFactor     - во сколько раз снижается количество эпох при предобучении в сравнении с epochs;</li>
 *  <li>{@link NetworkBuilder}   - билдер сетей с заданными настройками;</li>
 *  <li>{@link Optimizer}        - оптимизатор сети;</li>
 *  <li>{@link QueriesRangeType} - тип стратегии опросов</li>
 * </ul></pre>
 */
@YamlSerializable
public class FitParameters implements DeepCopyable {
    private final Dataset dataset;
    @YamlField private final int epochs;
    @YamlField private final int batchSize;
    @YamlField private final int queries;
    @YamlField private final boolean earlyStopping;
    @YamlField private final int earlyStoppingThreshold;
    @YamlField private final String doubleFormat;
    @YamlField private final boolean preTrainRequired;
    @YamlField private final int preTrainsCount;
    @YamlField private final double preTrainReduceFactor;
    @YamlField private final NetworkBuilder networkBuilder;
    @YamlField private final OptimizerBuilder optimizerBuilder;
    @YamlField private final QueriesRangeType queriesRangeType;

    /**
     * Конструктор, см. описание в {@link FitParameters}
     */
    public FitParameters(Dataset dataset, int epochs, int batchSize, int queries, boolean earlyStopping, int earlyStoppingThreshold, String doubleFormat, boolean preTrainRequired, int preTrainsCount, double preTrainReduceFactor, NetworkBuilder networkBuilder, OptimizerBuilder optimizerBuilder, QueriesRangeType queriesRangeType) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.earlyStoppingThreshold = earlyStoppingThreshold;
        this.doubleFormat = doubleFormat;
        this.preTrainRequired = preTrainRequired;
        this.preTrainsCount = preTrainsCount;
        this.preTrainReduceFactor = preTrainReduceFactor;
        this.networkBuilder = networkBuilder;
        this.optimizerBuilder = optimizerBuilder;
        this.queriesRangeType = queriesRangeType;
    }

    /**
     * Конструктор для сериализации 
     */
    private FitParameters() {
        this(null,
                0,
                0,
                0,
                false,
                0,
                null,
                false,
                0,
                0,
                null,
                null,
                null);
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

    public int getEarlyStoppingThreshold() {
        return earlyStoppingThreshold;
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
        return builder()
                .epochs((int) (epochs / preTrainReduceFactor))
                .queries(1)
                .earlyStopping(false)
                .build();
    }

    @Override
    public FitParameters deepCopy() {
        return builder().build();
    }

    @Override
    public String toString() {
        return "FitParameters{" +
                "dataset=" + dataset +
                ", epochs=" + epochs +
                ", batchSize=" + batchSize +
                ", queries=" + queries +
                ", earlyStopping=" + earlyStopping +
                ", earlyStoppingThreshold=" + earlyStoppingThreshold +
                ", doubleFormat='" + doubleFormat + '\'' +
                ", preTrainRequired=" + preTrainRequired +
                ", preTrainsCount=" + preTrainsCount +
                ", preTrainReduceFactor=" + preTrainReduceFactor +
                ", networkBuilder=" + networkBuilder +
                ", optimizerBuilder=" + optimizerBuilder +
                ", queriesRangeType=" + queriesRangeType +
                '}';
    }

    /**
     * Получение билдера на основе текущего экземпляра
     * @return билдер с заполненными полями
     */
    public FitParametersBuilder builder() {
        return new FitParametersBuilder(this);
    }

    /**
     * Получение нового билдера
     * @return пустой билдер
     */
    public static FitParametersBuilder newBuilder() {
        return new FitParametersBuilder();
    }
}
