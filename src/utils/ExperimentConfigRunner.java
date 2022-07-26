package utils;

import com.sun.istack.internal.NotNull;
import models.exceptions.SerializationException;
import options.Constants;

import java.util.*;
import java.util.logging.Logger;

public class ExperimentConfigRunner extends Thread {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static final String NAME_PATTERN = "collection-thread-%s";
    private final MyId myId;

    private final RunConfiguration runConfiguration;
    private final List<TrainResults> trainResults;
    private TrainResults bestTrainResults;

    public ExperimentConfigRunner(@NotNull RunConfiguration runConfiguration, @NotNull MyId parentMyId) {
        this.runConfiguration = runConfiguration;

        trainResults = new ArrayList<>();
        bestTrainResults = null;

        myId = MyId.buildNewFromParent(parentMyId);
    }

    public RunConfiguration getRunConfiguration() {
        return runConfiguration;
    }

    public List<TrainResults> getTrainResults() {
        return trainResults;
    }

    public TrainResults getBestTrainResults() {
        return bestTrainResults;
    }

    public MyId getMyId() {
        return myId;
    }

    @Override
    public void run() {
        logger.fine("Запуск потока");
        logger.finer("Запуск обработки конфигурации: " + runConfiguration.toString(Constants.DEBUG_MODE));
        logger.fine("Всего будет выполнено перезапусков: " + runConfiguration.getRetries());

        HashSet<RunConfigRunner> threads = new HashSet<>();
        for (int i = 0; i < runConfiguration.getRetries(); i++) {
            RunConfiguration clone = runConfiguration.copy();
            RunConfigRunner thread = new RunConfigRunner(clone, myId);
            thread.setName(String.format(RunConfigRunner.NAME_PATTERN, thread.getMyId().toThreadId()));
            threads.add(thread);
            thread.start();
        }

        Utils.joinThreads(threads);

        threads.forEach(thread -> trainResults.add(thread.getTrainResults()));

        bestTrainResults = trainResults.stream()
                .min(Comparator.comparingDouble(TrainResults::getMaxAbsoluteError))
                .orElseThrow(() -> new RuntimeException("Ошибка во время обработки результатов"));

        if (Constants.PRINT_REQUIRED && Constants.PRINT_EACH_CONFIGURATION_BEST.isRequired())
            logger.info(Utils.runConfigurationAndTrainResultsToString(
                    String.format("Наилучшие результаты обучения для конфигурации [[%s]]", runConfiguration.getDescription()),
                    runConfiguration, bestTrainResults, Constants.PRINT_EACH_CONFIGURATION_BEST, Constants.DEBUG_MODE,
                    Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        if (Constants.SAVE_REQUIRED && Constants.SAVE_EACH_CONFIGURATION_BEST) {
            bestTrainResults.getNetwork().clear();
            try {
                Utils.save(bestTrainResults.getNetwork(), Constants.SAVE_FOLDER,
                        String.format(Constants.SAVE_NETWORK_PATTERN,
                                runConfiguration.getMyId().getUid() + '_' + System.currentTimeMillis()));
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.finer("Завершение обработки конфигурации: " + runConfiguration.toString(Constants.DEBUG_MODE));
        logger.fine("Завершение потока");
    }
}
