package models.trainers;

import models.data.DataLoader;
import models.data.Dataset;
import models.data.LoadParameters;
import models.networks.NetworkBuilder;
import models.optimizers.OptimizerBuilder;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;

/**
 * Билдер для параметров обучения, см. {@link FitParameters}. Поддерживает значения по умолчанию. 
 * При билде загружает выборку, если она не была указана.
 */
@YamlSerializable
public class FitParametersBuilder implements Serializable, DeepCopyable {
    private final static int defaultBatchSize = 64;
    private final static int defaultQueries = 10;
    private final static boolean defaultEarlyStopping = true;
    private final static int defaultEarlyStoppingThreshold = 2;
    private final static String defaultDoubleFormat = "%13.10f";
    private final static int defaultPreTrainsCount = 3;
    private final static double defaultPreTrainReduceFactor = 10.0;
    private final static QueriesRangeType defaultQueriesRangeType = QueriesRangeType.NON_LINEAR;

    private Dataset dataset;
    @YamlField private DataLoader dataLoader;
    @YamlField private LoadParameters loadParameters;
    @YamlField private int epochs;
    @YamlField private int batchSize;
    @YamlField private int queries;
    @YamlField private boolean earlyStopping;
    @YamlField private boolean earlyStoppingTriggered;
    @YamlField private int earlyStoppingThreshold;
    @YamlField private String doubleFormat;
    @YamlField private boolean preTrainRequired;
    @YamlField private int preTrainsCount;
    @YamlField private double preTrainReduceFactor;
    @YamlField private NetworkBuilder networkBuilder;
    @YamlField private OptimizerBuilder optimizerBuilder;
    @YamlField private QueriesRangeType queriesRangeType;
    
    public FitParametersBuilder() {
    }

    public FitParametersBuilder(FitParameters fitParameters) {
        this.dataset = fitParameters.getDataset().deepCopy();
        this.epochs = fitParameters.getEpochs();
        this.batchSize = fitParameters.getBatchSize();
        this.queries = fitParameters.getQueries();
        this.earlyStopping = fitParameters.isEarlyStopping();
        this.earlyStoppingThreshold = fitParameters.getEarlyStoppingThreshold();
        this.doubleFormat = fitParameters.getDoubleFormat();
        this.preTrainRequired = fitParameters.isPreTrainRequired();
        this.preTrainsCount = fitParameters.getPreTrainsCount();
        this.preTrainReduceFactor = fitParameters.getPreTrainReduceFactor();
        this.networkBuilder = fitParameters.getNetworkBuilder().deepCopy();
        this.optimizerBuilder = fitParameters.getOptimizerBuilder().deepCopy();
        this.queriesRangeType = fitParameters.getQueriesRangeType();
    }
    
    public FitParametersBuilder dataset(Dataset dataset) {
        this.dataset = dataset;
        return this;
    }
    
    public FitParametersBuilder dataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
        return this;
    }
    
    public FitParametersBuilder loadParameters(LoadParameters loadParameters) {
        this.loadParameters = loadParameters;
        return this;
    }
    
    public FitParametersBuilder epochs(int epochs) {
        this.epochs = epochs;
        return this;
    }
    
    public FitParametersBuilder batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    public FitParametersBuilder queries(int queries) {
        this.queries = queries;
        return this;
    }
    
    public FitParametersBuilder earlyStopping(boolean earlyStopping) {
        this.earlyStopping = earlyStopping;
        this.earlyStoppingTriggered = true;
        return this;
    }

    public FitParametersBuilder earlyStoppingThreshold(int earlyStoppingThreshold) {
        this.earlyStoppingThreshold = earlyStoppingThreshold;
        return this;
    }
    
    public FitParametersBuilder doubleFormat(String doubleFormat) {
        this.doubleFormat = doubleFormat;
        return this;
    }
    
    public FitParametersBuilder preTrainRequired(boolean preTrainRequired) {
        this.preTrainRequired = preTrainRequired;
        return this;
    }
    
    public FitParametersBuilder preTrainsCount(int preTrainsCount) {
        this.preTrainsCount = preTrainsCount;
        return this;
    }
    
    public FitParametersBuilder preTrainReduceFactor(double preTrainReduceFactor) {
        this.preTrainReduceFactor = preTrainReduceFactor;
        return this;
    }
    
    public FitParametersBuilder networkBuilder(NetworkBuilder networkBuilder) {
        this.networkBuilder = networkBuilder;
        return this;
    }
    
    public FitParametersBuilder optimizerBuilder(OptimizerBuilder optimizerBuilder) {
        this.optimizerBuilder = optimizerBuilder;
        return this;
    }
    
    public FitParametersBuilder queriesRangeType(QueriesRangeType queriesRangeType) {
        this.queriesRangeType = queriesRangeType;
        return this;
    }

    /**
     * Построение параметров обучения
     * @return параметры обучения с указанными значениями
     */
    public FitParameters build() {
        validate();
        prepare();
        return new FitParameters(dataset.deepCopy(), epochs, batchSize, queries, earlyStopping, earlyStoppingThreshold,
                doubleFormat, preTrainRequired, preTrainsCount, preTrainReduceFactor, networkBuilder.deepCopy(),
                optimizerBuilder.deepCopy(), queriesRangeType);
    }

    /**
     * Проверка заданных значений
     */
    private void validate() {
        if ((dataset == null && (dataLoader == null || loadParameters == null)) ||
                networkBuilder == null || optimizerBuilder == null ||
                epochs < 1)
            throw ExceptionUtils.newWrongBuilderException(this.toString());
    }

    /**
     * Простановка значений по умолчанию, загрузка обучающей выборки при необходимости
     */
    private void prepare() {
        batchSize = batchSize < 1 ? defaultBatchSize : batchSize;
        queries = queries < 1 ? defaultQueries : queries;
        earlyStopping = earlyStoppingTriggered ? earlyStopping : defaultEarlyStopping;
        earlyStoppingThreshold = earlyStoppingThreshold < 1 ? defaultEarlyStoppingThreshold : earlyStoppingThreshold;
        doubleFormat = doubleFormat == null || doubleFormat.isEmpty() ? defaultDoubleFormat : doubleFormat;
        preTrainsCount = preTrainsCount < 1 ? defaultPreTrainsCount : preTrainsCount;
        preTrainReduceFactor = preTrainReduceFactor < 1.0 ? defaultPreTrainReduceFactor : preTrainReduceFactor;
        queriesRangeType = queriesRangeType == null ? defaultQueriesRangeType : queriesRangeType;
        if (dataset == null)
            dataset = dataLoader.load(loadParameters);

    }

    @Override
    public String toString() {
        return "FitParametersBuilder{" +
                "dataset=" + dataset +
                ", dataLoader=" + dataLoader +
                ", loadParameters=" + loadParameters +
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

    @Override
    public FitParametersBuilder deepCopy() {
        return new FitParametersBuilder()
                .dataset(dataset == null ? null : dataset.deepCopy())
                .dataLoader(dataLoader == null ? null : dataLoader.deepCopy())
                .loadParameters(loadParameters == null ? null : loadParameters.deepCopy())
                .epochs(epochs)
                .batchSize(batchSize)
                .queries(queries)
                .earlyStopping(earlyStopping)
                .earlyStoppingThreshold(earlyStoppingThreshold)
                .doubleFormat(doubleFormat)
                .preTrainRequired(preTrainRequired)
                .preTrainsCount(preTrainsCount)
                .preTrainReduceFactor(preTrainReduceFactor)
                .networkBuilder(networkBuilder == null ? null : networkBuilder.deepCopy())
                .optimizerBuilder(optimizerBuilder == null ? null : optimizerBuilder.deepCopy())
                .queriesRangeType(queriesRangeType);
    }
}
