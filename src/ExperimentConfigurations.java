import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.examples.Sin2X;
import models.data.approximation.functions.examples.SinX;
import models.data.approximation.functions.examples.SinX1_mul_X2;
import models.losses.MeanSquaredError;
import models.networks.Network;
import models.operations.activations.Linear;
import models.operations.activations.Tanh;
import models.optimizers.SGD;
import models.trainers.FitParameters;
import models.trainers.QueriesRangeType;
import options.Constants;
import options.DefaultParameters;
import utils.ExperimentConfiguration;
import utils.RunConfiguration;

import java.util.Arrays;
import java.util.List;

public abstract class ExperimentConfigurations {
    public static List<ExperimentConfiguration> getExperimentConfigurations() {
        return Arrays.asList(

                new ExperimentConfiguration("Исследование влияния размера выборки",
                        Arrays.asList(

                                new RunConfiguration(DefaultParameters.RETRIES, "Размер по умолчанию",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new Sin2X(Sin2X.getDefaultVariableRanges()),
                                                                DefaultParameters.SIZE,
                                                                DefaultParameters.TEST_PART,
                                                                DefaultParameters.VALID_PART,
                                                                DefaultParameters.EXTENDING_FACTOR)),
                                                DefaultParameters.EPOCHS,
                                                DefaultParameters.BATCH_SIZE,
                                                DefaultParameters.QUERIES,
                                                DefaultParameters.EARLY_STOPPING,
                                                Constants.DOUBLE_FORMAT,
                                                DefaultParameters.PRE_TRAIN_REQUIRED,
                                                DefaultParameters.PRE_TRAINS_COUNT,
                                                DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(1, 8, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(DefaultParameters.START_LR)
                                                        .stopLR(DefaultParameters.STOP_LR),
                                                QueriesRangeType.LINEAR
                                        )  // end FitParameters
                                ),  // end RunConfiguration

                            new RunConfiguration(DefaultParameters.RETRIES, "Размер * 2",
                                    new FitParameters(
                                            new ApproximationDataLoader().load(
                                                    new ApproximationLoadParameters(
                                                            new Sin2X(Sin2X.getDefaultVariableRanges()),
                                                            DefaultParameters.SIZE * 2,
                                                            DefaultParameters.TEST_PART,
                                                            DefaultParameters.VALID_PART,
                                                            DefaultParameters.EXTENDING_FACTOR)),
                                            DefaultParameters.EPOCHS,
                                            DefaultParameters.BATCH_SIZE,
                                            DefaultParameters.QUERIES,
                                            DefaultParameters.EARLY_STOPPING,
                                            Constants.DOUBLE_FORMAT,
                                            DefaultParameters.PRE_TRAIN_REQUIRED,
                                            DefaultParameters.PRE_TRAINS_COUNT,
                                            DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                            new Network.AnotherBuilder()
                                                    .sizes(Arrays.asList(1, 8, 1))
                                                    .activations(Arrays.asList(
                                                            new Tanh(),
                                                            new Linear()))
                                                    .loss(new MeanSquaredError())
                                                    .getBuilder(),
                                            new SGD.Builder()
                                                    .startLR(DefaultParameters.START_LR)
                                                    .stopLR(DefaultParameters.STOP_LR),
                                            QueriesRangeType.LINEAR
                                    )  // end FitParameters
                            ),  // end RunConfiguration

                            new RunConfiguration(DefaultParameters.RETRIES, "Размер * 4",
                                    new FitParameters(
                                            new ApproximationDataLoader().load(
                                                    new ApproximationLoadParameters(
                                                            new Sin2X(Sin2X.getDefaultVariableRanges()),
                                                            DefaultParameters.SIZE * 4,
                                                            DefaultParameters.TEST_PART,
                                                            DefaultParameters.VALID_PART,
                                                            DefaultParameters.EXTENDING_FACTOR)),
                                            DefaultParameters.EPOCHS,
                                            DefaultParameters.BATCH_SIZE,
                                            DefaultParameters.QUERIES,
                                            DefaultParameters.EARLY_STOPPING,
                                            Constants.DOUBLE_FORMAT,
                                            DefaultParameters.PRE_TRAIN_REQUIRED,
                                            DefaultParameters.PRE_TRAINS_COUNT,
                                            DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                            new Network.AnotherBuilder()
                                                    .sizes(Arrays.asList(1, 8, 1))
                                                    .activations(Arrays.asList(
                                                            new Tanh(),
                                                            new Linear()))
                                                    .loss(new MeanSquaredError())
                                                    .getBuilder(),
                                            new SGD.Builder()
                                                    .startLR(DefaultParameters.START_LR)
                                                    .stopLR(DefaultParameters.STOP_LR),
                                            QueriesRangeType.LINEAR
                                    )  // end FitParameters
                            )  // end RunConfiguration
                        )  // end List<RunConfiguration>
                ),  // end ExperimentConfiguration



                new ExperimentConfiguration("Исследование влияния количества эпох",
                        Arrays.asList(

                                new RunConfiguration(DefaultParameters.RETRIES, "Эпох по умолчанию",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new Sin2X(Sin2X.getDefaultVariableRanges()),
                                                                DefaultParameters.SIZE,
                                                                DefaultParameters.TEST_PART,
                                                                DefaultParameters.VALID_PART,
                                                                DefaultParameters.EXTENDING_FACTOR)),
                                                DefaultParameters.EPOCHS,
                                                DefaultParameters.BATCH_SIZE,
                                                DefaultParameters.QUERIES,
                                                DefaultParameters.EARLY_STOPPING,
                                                Constants.DOUBLE_FORMAT,
                                                DefaultParameters.PRE_TRAIN_REQUIRED,
                                                DefaultParameters.PRE_TRAINS_COUNT,
                                                DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(1, 8, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(DefaultParameters.START_LR)
                                                        .stopLR(DefaultParameters.STOP_LR),
                                                QueriesRangeType.LINEAR
                                        )  // end FitParameters
                                ),  // end RunConfiguration

                                new RunConfiguration(DefaultParameters.RETRIES, "Эпох * 2",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new Sin2X(Sin2X.getDefaultVariableRanges()),
                                                                DefaultParameters.SIZE,
                                                                DefaultParameters.TEST_PART,
                                                                DefaultParameters.VALID_PART,
                                                                DefaultParameters.EXTENDING_FACTOR)),
                                                DefaultParameters.EPOCHS * 2,
                                                DefaultParameters.BATCH_SIZE,
                                                DefaultParameters.QUERIES,
                                                DefaultParameters.EARLY_STOPPING,
                                                Constants.DOUBLE_FORMAT,
                                                DefaultParameters.PRE_TRAIN_REQUIRED,
                                                DefaultParameters.PRE_TRAINS_COUNT,
                                                DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(1, 8, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(DefaultParameters.START_LR)
                                                        .stopLR(DefaultParameters.STOP_LR),
                                                QueriesRangeType.LINEAR
                                        )  // end FitParameters
                                ),  // end RunConfiguration

                                new RunConfiguration(DefaultParameters.RETRIES, "Эпох * 4",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new Sin2X(Sin2X.getDefaultVariableRanges()),
                                                                DefaultParameters.SIZE,
                                                                DefaultParameters.TEST_PART,
                                                                DefaultParameters.VALID_PART,
                                                                DefaultParameters.EXTENDING_FACTOR)),
                                                DefaultParameters.EPOCHS * 4,
                                                DefaultParameters.BATCH_SIZE,
                                                DefaultParameters.QUERIES,
                                                DefaultParameters.EARLY_STOPPING,
                                                Constants.DOUBLE_FORMAT,
                                                DefaultParameters.PRE_TRAIN_REQUIRED,
                                                DefaultParameters.PRE_TRAINS_COUNT,
                                                DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(1, 8, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(DefaultParameters.START_LR)
                                                        .stopLR(DefaultParameters.STOP_LR),
                                                QueriesRangeType.LINEAR
                                        )  // end FitParameters
                                )  // end RunConfiguration
                        )  // end List<RunConfiguration>
                )  // end ExperimentConfiguration

        );  // end List<ExperimentConfiguration>
    }

    public static List<ExperimentConfiguration> getTwoDefaultExperimentConfigurations() {
        return Arrays.asList(
                new ExperimentConfiguration("Тестовый эксперимент #1",
                        Arrays.asList(
                                new RunConfiguration(2, "Конфигурация #1.1",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new SinX(SinX.getDefaultVariableRanges()),
                                                                128,
                                                                0.5,
                                                                0.25,
                                                                1.1
                                                        )
                                                ),
                                                1000,
                                                64,
                                                10,
                                                true,
                                                Constants.DOUBLE_FORMAT,
                                                true,
                                                2,
                                                5.0,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(1, 8, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.001),
                                                QueriesRangeType.NON_LINEAR
                                        )
                                ),

                                new RunConfiguration(3, "Конфигурация #1.2",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new SinX(SinX.getDefaultVariableRanges()),
                                                                256,
                                                                0.5,
                                                                0.25,
                                                                1.1
                                                        )
                                                ),
                                                500,
                                                64,
                                                10,
                                                true,
                                                Constants.DOUBLE_FORMAT,
                                                true,
                                                2,
                                                5.0,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(1, 8, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.001),
                                                QueriesRangeType.LINEAR
                                        )
                                )
                        )
                ),

                new ExperimentConfiguration("Тестовый эксперимент #2",
                        Arrays.asList(
                                new RunConfiguration(2, "Конфигурация #2.1",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new SinX1_mul_X2(SinX1_mul_X2.getDefaultVariableRanges()),
                                                                48,
                                                                0.5,
                                                                0.25,
                                                                1.05
                                                        )
                                                ),
                                                500,
                                                64,
                                                10,
                                                true,
                                                Constants.DOUBLE_FORMAT,
                                                true,
                                                2,
                                                5.0,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(2, 16, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.001),
                                                QueriesRangeType.NON_LINEAR
                                        )
                                ),

                                new RunConfiguration(3, "Конфигурация #2.2",
                                        new FitParameters(
                                                new ApproximationDataLoader().load(
                                                        new ApproximationLoadParameters(
                                                                new SinX1_mul_X2(SinX1_mul_X2.getDefaultVariableRanges()),
                                                                80,
                                                                0.5,
                                                                0.25,
                                                                1.05
                                                        )
                                                ),
                                                250,
                                                64,
                                                10,
                                                true,
                                                Constants.DOUBLE_FORMAT,
                                                true,
                                                2,
                                                5.0,
                                                new Network.AnotherBuilder()
                                                        .sizes(Arrays.asList(2, 16, 1))
                                                        .activations(Arrays.asList(
                                                                new Tanh(),
                                                                new Linear()))
                                                        .loss(new MeanSquaredError())
                                                        .getBuilder(),
                                                new SGD.Builder()
                                                        .startLR(0.1)
                                                        .stopLR(0.001),
                                                QueriesRangeType.NON_LINEAR
                                        )
                                )
                        )
                )
        );
    }
}
