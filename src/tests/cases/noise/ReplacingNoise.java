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
import models.losses.MeanSquaredError;
import models.networks.Network;
import models.operations.LinearActivation;
import models.operations.TanhActivation;
import models.optimizers.SGD;
import models.trainers.FitParameters;
import models.trainers.FitParametersBuilder;
import tests.TestStatus;

public class ReplacingNoise extends BaseForNoise{
    @Override
    public String getDescription() {
        return "Зашумление типа " + NoiseMode.REPLACING;
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

        final FitParametersBuilder fitParametersBuilder = FitParameters.newBuilder()
                .dataLoader(dataLoader)
                .optimizerBuilder(SGD.newBuilder().startLR(0.1).stopLR(0.0001))
                .networkBuilder(Network.newAnotherBuilder()
                        .loss(new MeanSquaredError())
                        .sizes(1, 8, 1)
                        .activations(new TanhActivation(), new LinearActivation()))
                .epochs(1000);

        final LoadParameters loadParametersNoNoise = approxLoadParametersBuilder.build();
        final Dataset datasetNoNoise = dataLoader.load(loadParametersNoNoise);
        final FitParameters fitParametersNoNoise = fitParametersBuilder.loadParameters(loadParametersNoNoise).build();

        final LoadParameters loadParametersReplacingNoise = approxLoadParametersBuilder.noiseMode(NoiseMode.REPLACING).build();
        final Dataset datasetReplacingNoise = dataLoader.load(loadParametersReplacingNoise);
        final FitParameters fitParametersReplacingNoise = fitParametersBuilder.loadParameters(loadParametersReplacingNoise).build();

        return datasetNoNoise.equals(datasetReplacingNoise) &&
               !fitParametersNoNoise.isRenoiseData() &&
                fitParametersReplacingNoise.isRenoiseData() ?
                TestStatus.PASSED : TestStatus.FAILED;
    }
}
