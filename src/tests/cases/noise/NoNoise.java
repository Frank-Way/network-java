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

public class NoNoise extends BaseForNoise {
    @Override
    public String getDescription() {
        return "Зашумление типа: " + NoiseMode.NONE;
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
        final LoadParameters loadParametersNoneNoise = approxLoadParametersBuilder.noiseMode(NoiseMode.NONE).build();
        final Dataset datasetNoneNoise = dataLoader.load(loadParametersNoneNoise);
        return datasetNoNoise.equals(datasetNoneNoise) ? TestStatus.PASSED : TestStatus.FAILED;
    }
}
