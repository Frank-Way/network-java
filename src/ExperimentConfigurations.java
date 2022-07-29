import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.examples.SinX;
import models.data.approximation.functions.examples.Sin2X;
import models.losses.MeanSquaredError;
import models.networks.NetworkBuilder;
import models.networks.NetworkBuilderParameters;
import models.operations.activations.Linear;
import models.operations.activations.Tanh;
import models.optimizers.SGD;
import models.trainers.FitParameters;
import options.Constants;
import options.DefaultParameters;
import utils.ExperimentConfiguration;
import utils.RunConfiguration;

import java.util.Arrays;
import java.util.List;

public class ExperimentConfigurations {
    public static final List<ExperimentConfiguration> experimentConfigurations = Arrays.asList(

            new ExperimentConfiguration("Исследование влияния предобучения",
                    Arrays.asList(

                            new RunConfiguration(DefaultParameters.RETRIES, "Без предобучения",
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
                                            false,
                                            DefaultParameters.PRE_TRAINS_COUNT,
                                            DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                            new NetworkBuilderParameters(
                                                    Arrays.asList(1, 8, 1),
                                                    Arrays.asList(new Tanh(), new Linear()),
                                                    new MeanSquaredError()
                                            ),  // end NetworkBuilderParameters
                                            new SGD(DefaultParameters.START_LR, DefaultParameters.STOP_LR)
                                    )  // end FitParameters
                            )/*,  // end RunConfiguration

                            new RunConfiguration(DefaultParameters.RETRIES, "C преодобучением",
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
                                            true,
                                            DefaultParameters.PRE_TRAINS_COUNT,
                                            DefaultParameters.PRE_TRAIN_REDUCE_FACTOR,
                                            new NetworkBuilderParameters(
                                                    Arrays.asList(1, 8, 1),
                                                    Arrays.asList(new Tanh(), new Linear()),
                                                    new MeanSquaredError()
                                            ),  // end NetworkBuilderParameters
                                            new SGD(DefaultParameters.START_LR, DefaultParameters.STOP_LR)
                                    )  // end FitParameters
                            )  // end RunConfiguration
                            */
                    )  // end List<RunConfiguration>
            )  // end ExperimentConfiguration

    );  // end List<ExperimentConfiguration>
}
