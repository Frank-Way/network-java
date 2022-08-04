package utils;

import com.sun.istack.internal.NotNull;
import models.exceptions.SerializationException;
import models.trainers.FitResults;
import options.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Обёртка вокруг {@link Thread} для запуска нескольких попыток обучения по одной конфигурации. Запускает дочерние
 *  потоки {@link RunConfigRunner} в количестве, равном атрибуту retries полученного {@link RunConfiguration}.
 * Параметры модели:
 *  {@link Logger} - логгер;
 *  NAME_PATTERN - шаблон имени потока;
 *  {@link MyId} - идентификатор;
 *  {@link RunConfiguration} - конфигурация запуска, для которой будет запущено <retries> попыток обучения;
 *  список<{@link FitResults}> - все результаты обучения;
 *  {@link FitResults} - наилучшие результаты обучения.
 */
public class ExperimentConfigRunner extends Thread {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String NAME_PATTERN = "collection-thread-%s";
    private final MyId myId;

    private final RunConfiguration runConfiguration;
    private final List<FitResults> fitResults;
    private FitResults bestFitResults;

    /**
     * Конструктор
     * @param runConfiguration  конфигурация запуска
     * @param parentMyId  идентификатор родителя
     */
    public ExperimentConfigRunner(@NotNull RunConfiguration runConfiguration, @NotNull MyId parentMyId) {
        this.runConfiguration = runConfiguration;

        fitResults = new ArrayList<>();
        bestFitResults = null;

        myId = MyId.buildNewFromParent(parentMyId);

        setName(String.format(NAME_PATTERN, myId.toThreadId()));
    }

    public RunConfiguration getRunConfiguration() {
        return runConfiguration;
    }

    public List<FitResults> getFitResults() {
        return fitResults;
    }

    public FitResults getBestFitResults() {
        return bestFitResults;
    }

    public MyId getMyId() {
        return myId;
    }

    /**
     * Запуск обучения заданной конфигурации
     */
    @Override
    public void run() {
        logger.finer("Запуск обработки конфигурации: " + runConfiguration.getDescription());
        logger.fine("Всего будет выполнено перезапусков: " + runConfiguration.getRetries());

        // формирование потоков для запуска обучения заданной конфигурации
        List<RunConfigRunner> threads = new ArrayList<>();
        for (int i = 0; i < runConfiguration.getRetries(); i++) {
            RunConfiguration clone = runConfiguration.copy();  // дочернему потоку отдаётся копия конфигурации
            RunConfigRunner thread = new RunConfigRunner(clone, myId);
            threads.add(thread);
        }

        Utils.startThreads(threads);  // запуск потоков

        Utils.joinThreads(threads);  // ожидание завершения потоков

        // получение всех результатов обучения из дочерних потоков
        threads.forEach(thread -> fitResults.add(thread.getFitResults()));

        // получение наилучших результатов обучения для заданной конфигурации
        bestFitResults = fitResults.stream()
                .min(Comparator.comparingDouble(FitResults::getMaxAbsoluteError))
                .orElseThrow(() -> new RuntimeException("Ошибка во время обработки результатов"));

        // вывод
        if (Constants.PRINT_REQUIRED && Constants.PRINT_EACH_CONFIGURATION_BEST.isRequired())
            logger.info(String.format("Наилучшие результаты обучения для конфигурации [[%s]]\n", runConfiguration.getDescription()) +
                    Utils.runConfigurationAndFitResultsToString(runConfiguration, bestFitResults,
                            Constants.PRINT_EACH_CONFIGURATION_BEST, Constants.DEBUG_MODE, Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        // сохранение
        if (Constants.SAVE_REQUIRED && Constants.SAVE_EACH_CONFIGURATION_BEST) {
            bestFitResults.getNetwork().clear();
            try {
                Utils.save(bestFitResults.getNetwork(), Constants.SAVE_FOLDER,
                        String.format(Constants.SAVE_NETWORK_PATTERN,
                                runConfiguration.getMyId().getUid() + '_' + System.currentTimeMillis()));
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.finer("Завершение обработки конфигурации: " + runConfiguration.getDescription());
    }
}
