import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.VariableRange;
import models.data.approximation.functions.examples.SinX1_mul_X2;
import models.data.approximation.functions.examples.SinX;
import models.layers.DenseLayer;
import models.losses.MeanSquaredError;
import models.networks.Network;
import models.operations.activations.Linear;
import models.operations.activations.Tanh;
import models.optimizers.SGD;
import models.trainers.FitParameters;
import utils.*;
import models.trainers.Trainer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;

public class Main {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        try {
            LogManager logManager = LogManager.getLogManager();
            Class<Main> aClass = Main.class;
            InputStream inputStream = aClass.getResourceAsStream("logging.properties");
            logManager.readConfiguration(inputStream);
            logger.fine("Успешно считаны конфигурации для логеров");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        List<ExperimentConfiguration> experimentConfigurations = new ArrayList<>();
        logger.fine("Начало считывания конфигураций экспериментов");
        experimentConfigurations.add(new ExperimentConfiguration("Исследование влияния размера выборки на точность обучения",
                Arrays.asList(
                        new RunConfiguration(DefaultParameters.RETRIES,
                                new FitParameters(new ApproximationDataLoader().load(new ApproximationLoadParameters(new SinX(Arrays.asList(new VariableRange(0, 6.28))), DefaultParameters.SIZE * 2, DefaultParameters.TEST_PART, DefaultParameters.VALID_PART, DefaultParameters.EXTENDING_FACTOR)), DefaultParameters.EPOCHS, DefaultParameters.BATCH_SIZE, DefaultParameters.QUERIES, DefaultParameters.EARLY_STOPPING, OverallConstants.DOUBLE_FORMAT),
                                new Trainer(new Network(Arrays.asList(new DenseLayer(1,8, new Tanh()), new DenseLayer(8,1, new Linear())), new MeanSquaredError()), new SGD(DefaultParameters.START_LR, DefaultParameters.STOP_LR))),

                        new RunConfiguration(DefaultParameters.RETRIES * 2,
                                new FitParameters(new ApproximationDataLoader().load(new ApproximationLoadParameters(new SinX(Arrays.asList(new VariableRange(0, 6.28))), DefaultParameters.SIZE, DefaultParameters.TEST_PART, DefaultParameters.VALID_PART, DefaultParameters.EXTENDING_FACTOR)), DefaultParameters.EPOCHS, DefaultParameters.BATCH_SIZE, DefaultParameters.QUERIES, DefaultParameters.EARLY_STOPPING, OverallConstants.DOUBLE_FORMAT),
                                new Trainer(new Network(Arrays.asList(new DenseLayer(1,8, new Tanh()), new DenseLayer(8,1, new Linear())), new MeanSquaredError()), new SGD(DefaultParameters.START_LR, DefaultParameters.STOP_LR))),
                        
                        new RunConfiguration(DefaultParameters.RETRIES,
                                new FitParameters(new ApproximationDataLoader().load(new ApproximationLoadParameters(new SinX(Arrays.asList(new VariableRange(0, 6.28))), DefaultParameters.SIZE * 3, DefaultParameters.TEST_PART, DefaultParameters.VALID_PART, DefaultParameters.EXTENDING_FACTOR)), DefaultParameters.EPOCHS, DefaultParameters.BATCH_SIZE, DefaultParameters.QUERIES, DefaultParameters.EARLY_STOPPING, OverallConstants.DOUBLE_FORMAT),
                                new Trainer(new Network(Arrays.asList(new DenseLayer(1,8, new Tanh()), new DenseLayer(8,1, new Linear())), new MeanSquaredError()), new SGD(DefaultParameters.START_LR, DefaultParameters.STOP_LR))),

                        new RunConfiguration(DefaultParameters.RETRIES * 3,
                                new FitParameters(new ApproximationDataLoader().load(new ApproximationLoadParameters(new SinX(Arrays.asList(new VariableRange(0, 6.28))), DefaultParameters.SIZE, DefaultParameters.TEST_PART, DefaultParameters.VALID_PART, DefaultParameters.EXTENDING_FACTOR)), DefaultParameters.EPOCHS, DefaultParameters.BATCH_SIZE, DefaultParameters.QUERIES, DefaultParameters.EARLY_STOPPING, OverallConstants.DOUBLE_FORMAT),
                                new Trainer(new Network(Arrays.asList(new DenseLayer(1,8, new Tanh()), new DenseLayer(8,1, new Linear())), new MeanSquaredError()), new SGD(DefaultParameters.START_LR, DefaultParameters.STOP_LR))))));

        logger.fine("Успешно считаны конфигурации экспериментов");
        logger.finer(experimentConfigurations.stream().map(ec -> ec.toString(OverallConstants.DEBUG_MODE)).collect(Collectors.toList()).toString());

        logger.fine("Начало запуска экспериментов");
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations) {
            experimentConfiguration.run();
        }
        logger.fine("Завершение запуска экспериментов");
        logger.finer(experimentConfigurations.stream()
                .map(ec -> Utils.mapToDebugString(ec.getResultsMap(), OverallConstants.DEBUG_MODE))
                .collect(Collectors.toList())
                .toString());

    }

}

