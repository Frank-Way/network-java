import models.data.approximation.ApproxDataLoader;
import models.data.approximation.ApproxLoadParameters;
import models.data.approximation.functions.impl.SinX;
import models.losses.MeanSquaredError;
import models.networks.Network;
import models.operations.LinearActivation;
import models.operations.Operation;
import models.operations.TanhActivation;
import models.optimizers.SGD;
import models.trainers.FitParametersBuilder;
import models.trainers.QueriesRangeType;
import serialization.SerializationType;
import serialization.SerializationUtils;
import serialization.exceptions.SerializationException;
import utils.automatization.ExperimentBuilder;
import utils.automatization.RunConfigurationBuilder;

import java.util.Arrays;

public abstract class Experiments {
    public static ExperimentBuilder[] getDefaultExperimentBuilders(String doubleFormat) {
        return new ExperimentBuilder[] {
                new ExperimentBuilder()
                        .description("Пример эксперимента 1")
                        .runConfigurationBuilders(
                                new RunConfigurationBuilder[] {
                                        new RunConfigurationBuilder()
                                                .description("Пример конфигурации 1.1")
                                                .retries(3)
                                                .fitParametersBuilder(
                                                        new FitParametersBuilder()
                                                                .networkBuilder(
                                                                        Network.newAnotherBuilder()
                                                                                .sizes(new int[] {
                                                                                        1, 8, 1})
                                                                                .activations(
                                                                                        new Operation[] {
                                                                                                new TanhActivation(),
                                                                                                new LinearActivation()
                                                                                        })
                                                                                .loss(new MeanSquaredError()))
                                                                .optimizerBuilder(SGD.newBuilder()
                                                                        .startLR(0.1)
                                                                        .stopLR(0.0001))
                                                                .dataLoader(new ApproxDataLoader())
                                                                .loadParameters(ApproxLoadParameters.newBuilder()
                                                                        .function(new SinX())
                                                                        .size(10)
                                                                        .build())
                                                                .doubleFormat(doubleFormat)
                                                                .epochs(10000)
                                                                .queriesRangeType(QueriesRangeType.NON_LINEAR)
                                        )
                                }
                )
        };
    }

    public static ExperimentBuilder[] getExperimentBuildersFromFile(String path, String filename,
                                                                    SerializationType serializationType)
            throws SerializationException {
        return Arrays.stream((Object[]) SerializationUtils.load(
                        ExperimentBuilder[].class, path, filename, serializationType))
                .map(ExperimentBuilder.class::cast).toArray(ExperimentBuilder[]::new);
    }
}