import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.impl.Sin2X;
import models.losses.MeanSquaredError;
import models.networks.AnotherNetworkBuilder;
import models.networks.Network;
import models.operations.LinearActivation;
import models.operations.Operation;
import models.operations.TanhActivation;
import models.optimizers.SGD;
import models.optimizers.SGDBuilder;
import models.trainers.FitParameters;
import models.trainers.QueriesRangeType;
import serialization.SerializationType;
import serialization.SerializationUtils;
import serialization.exceptions.SerializationException;
import utils.automatization.ExperimentConfiguration;
import utils.automatization.RunConfiguration;

import java.util.Arrays;

public abstract class ExperimentConfigurations {
    private static final String DEFAULT_DOUBLE_FORMAT = "%15.10f";

    public static ExperimentConfiguration[] getDefaultExperimentConfigurations() {
        return getDefaultExperimentConfigurations(DEFAULT_DOUBLE_FORMAT);
    }

    public static ExperimentConfiguration[] getDefaultExperimentConfigurations(String doubleFormat) {
        return new ExperimentConfiguration[]{
                new ExperimentConfiguration("Исследование влияния размера выборки",
                        new RunConfiguration[]{
                                new RunConfiguration(2, "1024 элементов",
                                        new FitParameters(
                                                new ApproximationDataLoader(),
                                                ApproximationLoadParameters.newBuilder()
                                                        .function(new Sin2X(Sin2X.getDefaultVariableRanges()))
                                                        .size(1024)
                                                        .testPart(0.5)
                                                        .validPart(0.25)
                                                        .extendingFactor(1.1)
                                                        .build(),
                                                500,
                                                64,
                                                10,
                                                true,
                                                doubleFormat,
                                                true,
                                                3,
                                                10,
                                                Network.anotherBuilder()
                                                        .sizes(new int[]{
                                                                1,
                                                                8,
                                                                1
                                                        })
                                                        .activations(new Operation[] {
                                                                new TanhActivation(),
                                                                new LinearActivation()
                                                        })
                                                        .loss(new MeanSquaredError()),
                                                SGD.builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.0001),
                                                QueriesRangeType.NON_LINEAR
                                        )  // new FitParameters
                                ),  // new RunConfiguration
                                new RunConfiguration(2, "2048 элементов",
                                        new FitParameters(
                                                new ApproximationDataLoader(),
                                                ApproximationLoadParameters.newBuilder()
                                                        .function(new Sin2X(Sin2X.getDefaultVariableRanges()))
                                                        .size(2048)
                                                        .testPart(0.5)
                                                        .validPart(0.25)
                                                        .extendingFactor(1.1)
                                                        .build(),
                                                500,
                                                64,
                                                10,
                                                true,
                                                doubleFormat,
                                                true,
                                                3,
                                                10,
                                                Network.anotherBuilder()
                                                        .sizes(new int[]{
                                                                1,
                                                                8,
                                                                1
                                                        })
                                                        .activations(new Operation[] {
                                                                new TanhActivation(),
                                                                new LinearActivation()
                                                        })
                                                        .loss(new MeanSquaredError()),
                                                SGD.builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.0001),
                                                QueriesRangeType.NON_LINEAR
                                        )  // new FitParameters
                                ),  // new RunConfiguration
                                new RunConfiguration(2, "4096 элементов",
                                        new FitParameters(
                                                new ApproximationDataLoader(),
                                                ApproximationLoadParameters.newBuilder()
                                                        .function(new Sin2X(Sin2X.getDefaultVariableRanges()))
                                                        .size(4096)
                                                        .testPart(0.5)
                                                        .validPart(0.25)
                                                        .extendingFactor(1.1)
                                                        .build(),
                                                500,
                                                64,
                                                10,
                                                true,
                                                doubleFormat,
                                                true,
                                                3,
                                                10,
                                                Network.anotherBuilder()
                                                        .sizes(new int[]{
                                                                1,
                                                                8,
                                                                1
                                                        })
                                                        .activations(new Operation[] {
                                                                new TanhActivation(),
                                                                new LinearActivation()
                                                        })
                                                        .loss(new MeanSquaredError()),
                                                SGD.builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.0001),
                                                QueriesRangeType.NON_LINEAR
                                        )  // new FitParameters
                                )  // new RunConfiguration
                        }  // new RunConfiguration[]
                ),  // new ExperimentConfiguration
                new ExperimentConfiguration("Исследование влияния длительности обучения",
                        new RunConfiguration[]{
                                new RunConfiguration(2, "500 эпох",
                                        new FitParameters(
                                                new ApproximationDataLoader(),
                                                ApproximationLoadParameters.newBuilder()
                                                        .function(new Sin2X(Sin2X.getDefaultVariableRanges()))
                                                        .size(1024)
                                                        .testPart(0.5)
                                                        .validPart(0.25)
                                                        .extendingFactor(1.1)
                                                        .build(),
                                                500,
                                                64,
                                                10,
                                                true,
                                                doubleFormat,
                                                true,
                                                3,
                                                10,
                                                Network.anotherBuilder()
                                                        .sizes(new int[]{
                                                                1,
                                                                8,
                                                                1
                                                        })
                                                        .activations(new Operation[] {
                                                                new TanhActivation(),
                                                                new LinearActivation()
                                                        })
                                                        .loss(new MeanSquaredError()),
                                                SGD.builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.0001),
                                                QueriesRangeType.NON_LINEAR
                                        )  // new FitParameters
                                ),  // new RunConfiguration
                                new RunConfiguration(2, "1000 эпох",
                                        new FitParameters(
                                                new ApproximationDataLoader(),
                                                ApproximationLoadParameters.newBuilder()
                                                        .function(new Sin2X(Sin2X.getDefaultVariableRanges()))
                                                        .size(1024)
                                                        .testPart(0.5)
                                                        .validPart(0.25)
                                                        .extendingFactor(1.1)
                                                        .build(),
                                                1000,
                                                64,
                                                10,
                                                true,
                                                doubleFormat,
                                                true,
                                                3,
                                                10,
                                                Network.anotherBuilder()
                                                        .sizes(new int[]{
                                                                1,
                                                                8,
                                                                1
                                                        })
                                                        .activations(new Operation[] {
                                                                new TanhActivation(),
                                                                new LinearActivation()
                                                        })
                                                        .loss(new MeanSquaredError()),
                                                SGD.builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.0001),
                                                QueriesRangeType.NON_LINEAR
                                        )  // new FitParameters
                                ),  // new RunConfiguration
                                new RunConfiguration(2, "2000 эпох",
                                        new FitParameters(
                                                new ApproximationDataLoader(),
                                                ApproximationLoadParameters.newBuilder()
                                                        .function(new Sin2X(Sin2X.getDefaultVariableRanges()))
                                                        .size(1024)
                                                        .testPart(0.5)
                                                        .validPart(0.25)
                                                        .extendingFactor(1.1)
                                                        .build(),
                                                2000,
                                                64,
                                                10,
                                                true,
                                                doubleFormat,
                                                true,
                                                3,
                                                10,
                                                Network.anotherBuilder()
                                                        .sizes(new int[]{
                                                                1,
                                                                8,
                                                                1
                                                        })
                                                        .activations(new Operation[] {
                                                                new TanhActivation(),
                                                                new LinearActivation()
                                                        })
                                                        .loss(new MeanSquaredError()),
                                                SGD.builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.0001),
                                                QueriesRangeType.NON_LINEAR
                                        )  // new FitParameters
                                )  // new RunConfiguration
                        }  // new RunConfiguration[]
                )  // new ExperimentConfiguration
        };  // new ExperimentConfiguration[]
    }

    public static ExperimentConfiguration[] getExperimentConfigurationsFromFile(String path, String filename,
                                                                                SerializationType serializationType) throws SerializationException {
        return Arrays.stream((Object[]) SerializationUtils.load(
                    ExperimentConfiguration[].class, path, filename, serializationType))
                .map(ExperimentConfiguration.class::cast).toArray(ExperimentConfiguration[]::new);
    }
}
