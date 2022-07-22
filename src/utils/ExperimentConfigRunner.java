package utils;

import com.sun.istack.internal.NotNull;
import options.OverallConstants;

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
        logger.finer("Запуск обработки конфигурации: " + runConfiguration.toString(OverallConstants.DEBUG_MODE));
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

        Utils.print(String.format("Наилучшие результаты обучения для конфигурации [[%s]]", runConfiguration.getMyId()),
                runConfiguration, bestTrainResults,
                OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_EACH_CONFIGURATION_BEST.isRequired(),
                OverallConstants.PRINT_EACH_CONFIGURATION_BEST);

        logger.finer("Завершение обработки конфигурации: " + runConfiguration.toString(OverallConstants.DEBUG_MODE));
        logger.fine("Завершение потока");
    }
}
