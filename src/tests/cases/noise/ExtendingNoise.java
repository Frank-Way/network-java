package tests.cases.noise;

import models.data.DataLoader;
import models.data.Dataset;
import models.data.LoadParameters;
import models.data.approximation.ApproxDataLoader;
import models.data.approximation.ApproxLoadParameters;
import models.data.approximation.ApproxLoadParametersBuilder;
import models.data.approximation.NoiseMode;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.impl.SinX;
import tests.TestStatus;

public class ExtendingNoise extends BaseForNoise{
    @Override
    public String getDescription() {
        return "Зашумление типа: " + NoiseMode.EXTENDING;
    }

    @Override
    public TestStatus process(Object... args) {
        System.out.println(getId() + " [" + getDescription() + "]");
        final int size = 100;
        final Function function = new SinX();
        final DataLoader dataLoader = new ApproxDataLoader();
        final ApproxLoadParametersBuilder approxLoadParametersBuilder = ApproxLoadParameters.newBuilder()
                .noiseMode(null)
                .size(size)
                .function(function);
        final LoadParameters loadParametersNoNoise = approxLoadParametersBuilder.build();
        final Dataset datasetNoNoise = dataLoader.load(loadParametersNoNoise);
        final LoadParameters loadParametersExtendingNoise = approxLoadParametersBuilder.noiseMode(NoiseMode.EXTENDING).build();
        final Dataset datasetExtendingNoise = dataLoader.load(loadParametersExtendingNoise);
        return !datasetNoNoise.equals(datasetExtendingNoise) &&
                datasetExtendingNoise.getRows() == 2 * datasetNoNoise.getRows() ?
                TestStatus.PASSED : TestStatus.FAILED;
    }
}
