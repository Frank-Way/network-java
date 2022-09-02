package models.trainers;

import models.data.DataLoader;
import models.data.Dataset;
import models.data.LoadParameters;
import models.networks.NetworkBuilder;
import models.optimizers.OptimizerBuilder;

public class FitParametersBuilder {
    private Dataset dataset;
    private DataLoader dataLoader;
    private LoadParameters loadParameters;
    private int epochs;
    private int batchSize;
    private int queries;
    private boolean earlyStopping;
    private String doubleFormat;
    private boolean preTrainRequired;
    private int preTrainsCount;
    private double preTrainReduceFactor;
    private NetworkBuilder networkBuilder;
    private OptimizerBuilder optimizerBuilder;
    private QueriesRangeType queriesRangeType;
    
    public FitParametersBuilder() {
        
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

    public FitParameters build() {
        validate();
        prepare();
        return new FitParameters(dataset, dataLoader, loadParameters, )
    }

    protected void validate() {
        if ((dataset == null && (dataLoader == null || loadParameters == null)) ||
                networkBuilder == null || optimizerBuilder == null)
            throw new IllegalStateException("Некорректное состояние билдера: " + this);
    }

    protected void prepare() {

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
