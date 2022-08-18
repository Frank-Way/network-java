package utils;

import com.sun.istack.internal.NotNull;
import models.data.Data;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.networks.Network;
import models.operations.BiasAdd;
import models.operations.WeightMultiply;
import models.trainers.FitResults;
import options.PrintOptions;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Утилитарные методы, используемые в различных частях проекта
 */
public abstract class Utils {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Получение строки с описанием конфигурации запуска и результатов обучения, представленных, соответственно,
     *  объектами {@link RunConfiguration} и {@link FitResults}
     * @param runConfiguration  конфигурация обучения
     * @param results  результаты обучения
     * @param printOptions  опции вывода
     * @param debugMode  режим вывода
     * @param doubleFormat  формат вывода вещественных чисел
     * @return  строка с требуемыми значениями
     */
    public static String runConfigurationAndFitResultsToString(@NotNull RunConfiguration runConfiguration,
                                                               @NotNull FitResults results,
                                                               @NotNull PrintOptions printOptions,
                                                               boolean debugMode,
                                                               String doubleFormat) {
        StringBuilder sb = new StringBuilder();

        sb.append("конфигурация:\n").append(runConfiguration.toString(debugMode)).append("\n");
        sb.append("результаты:\n").append(results.toString(debugMode)).append("\n");

        if (printOptions.isParametersRequired())
            sb.append("параметры:\n")
                    .append(networkParametersToString(results.getNetwork(), doubleFormat)).append("\n");

        if (printOptions.isTableRequired())
            sb.append("таблица:\n")
                    .append(networkOutputToTable(runConfiguration.getFitParameters().getDataset().getValidData(),
                            results.getNetwork(), printOptions.getTablePart(), doubleFormat)).append("\n");

        if (printOptions.isDynamicRequired())
            sb.append("динамика:\n")
                    .append(trainDynamicToTable(results.getTestLossesMap(), doubleFormat)).append("\n");

        return sb.toString();
    }

    /**
     * Формирование строки с параметрами сети по слоям
     * @param network  сеть
     * @param doubleFormat  формат вывода вещественных чисел
     * @return  строка с описанием параметров сети
     */
    public static String networkParametersToString(Network network, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        for (int layer = 0; layer < network.layersCount(); layer++) {
            sb.append("Слой ").append(layer + 1).append("\n");

            Matrix weight = network.getLayer(layer).getParameter(WeightMultiply.class);
            sb.append("Веса: [").append(weight.getRows()).append(" x ").append(weight.getCols()).append("]\n");
            sb.append(weight.valuesToString(doubleFormat)).append("\n");

            Matrix bias = network.getLayer(layer).getParameter(BiasAdd.class);
            sb.append("Смещения: [").append(bias.getRows()).append(" x ").append(bias.getCols()).append("]\n");
            sb.append(bias.valuesToString(doubleFormat)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Формирование таблицы с результатами работы сети
     * @param data  выборка для проверки
     * @param network  сеть
     * @param part  какую часть таблицы необходимо вывести
     * @param doubleFormat  формат вывода вещественных чисел
     * @return  строка с таблицей
     */
    public static String networkOutputToTable(Data data, Network network, double part, String doubleFormat) {
        Matrix x = data.getInputs();
        Matrix t = data.getOutputs();
        Matrix y = network.forward(x);
        Matrix e = t.sub(y).abs();
        return networkIOToStringTable(x, t, y, e, part, doubleFormat);
    }

    /**
     * Формирование таблицы зависимости потери от эпохи
     * @param map  мапа, где ключ - номер эпохи, а значение - потеря
     * @param doubleFormat  формат вывода вещественных чисел
     * @return  строка с таблицей
     */
    public static String trainDynamicToTable(Map<Integer, Double> map, String doubleFormat) {
        List<String> xHeaders = Collections.singletonList("эпоха");
        List<String> yHeaders = Collections.singletonList("потеря");
        int rows = map.size();
        double[][] xValuesDA = new double[rows][1];
        double[][] yValuesDA = new double[rows][1];
        List<Integer> epochs = map.keySet().stream().sorted().collect(Collectors.toList());
        for (int row = 0; row < epochs.size(); row++) {
            xValuesDA[row][0] = epochs.get(row);
            yValuesDA[row][0] = map.get(epochs.get(row));
        }
        Matrix xValues = new Matrix(xValuesDA);
        Matrix yValues = new Matrix(yValuesDA);
        return buildStringTableFromHeadersAndBody(xHeaders, xValues, yHeaders, yValues, 1.0, doubleFormat);
    }

    /**
     * Формирование таблицы с результатами работы сети
     * @param inputs  входные значения
     * @param targets  требуемые выходные значения
     * @param predictions  результаты вычислений сети
     * @param errors  ошибки вычисления сети
     * @param part  какую часть таблицы необходимо вывести
     * @param doubleFormat  формат вывода вещественных чисел
     * @return  строка с таблицей
     */
    public static String networkIOToStringTable(@NotNull Matrix inputs,
                                                @NotNull Matrix targets,
                                                @NotNull Matrix predictions,
                                                @NotNull Matrix errors,
                                                double part, String doubleFormat) {
        List<String> xHeaders = new ArrayList<>();
        List<String> yHeaders = new ArrayList<>();

        for (int i = 0; i < inputs.getCols(); i++)
            xHeaders.add("x" + (i + 1));
        for (int i = 0; i < targets.getCols(); i++) {
            int j = i + 1;
            yHeaders.add("t" + j);
            yHeaders.add("y" + j);
            yHeaders.add("|t" + j + "-y" + j + "|");
        }
        Matrix yValues;
        int col = 0;
        do {
            yValues = targets.getCol(col).stack(predictions.getCol(col), 0).stack(errors.getCol(col), 0);
            col++;
        } while (col < targets.getCols());
        return buildStringTableFromHeadersAndBody(xHeaders, inputs, yHeaders, yValues, part, doubleFormat);
    }

    /**
     * Формирование таблицы по заголовкам и телу
     * @param xHeaders  заголовки агрументов
     * @param xValues  аргументы
     * @param yHeaders  заголовки значений
     * @param yValues  значения
     * @param part  какую часть таблицы необходимо вывести
     * @param doubleFormat  формат вывода вещественных чисел
     * @return  строка с таблицей
     */
    public static String buildStringTableFromHeadersAndBody(@NotNull List<String> xHeaders,
                                                            @NotNull Matrix xValues,
                                                            @NotNull List<String> yHeaders,
                                                            @NotNull Matrix yValues,
                                                            double part, String doubleFormat) {
        char mainSpliterator = '|';
        char separatorBase = '-';
        char separatorSpliterator = '+';
        StringBuilder separatorSB = new StringBuilder();
        StringBuilder bodySB = new StringBuilder();
        String format = " " + doubleFormat + " ";
        int cellWidth = 2 + Integer.parseInt(doubleFormat.substring(
                doubleFormat.indexOf("%") + 1,
                doubleFormat.indexOf(".")));
        StringBuilder headerSB = new StringBuilder(joinHeader(xHeaders, cellWidth));
        headerSB.append(mainSpliterator);
        headerSB.append(joinHeader(yHeaders, cellWidth));
        for (int i = 0; i < headerSB.toString().indexOf(mainSpliterator); i++)
            separatorSB.append(separatorBase);
        separatorSB.append(separatorSpliterator);
        while (separatorSB.length() < headerSB.length())
            separatorSB.append(separatorBase);
        int[] indices = MatrixOperations.getLinSpace(0, xValues.getRows() - 1, (int)Math.round(xValues.getRows() * part));
        for (int row: indices) {
            for (int col = 0; col < xValues.getCols(); col++)
                bodySB.append(String.format(format, xValues.getValue(row, col)));
            bodySB.append(mainSpliterator);
            for (int col = 0; col < yValues.getCols(); col++)
                bodySB.append(String.format(format, yValues.getValue(row, col)));
            bodySB.append("\n");
        }
        return String.join("\n", headerSB.toString(), separatorSB.toString(), bodySB.toString());
    }

    /**
     * Преобразование мапы в строку, где ключ и значение реализуют интерфейс Debuggable
     * @param map  мапа
     * @param debugMode  режим
     * @param <T>  ключ, реализующий Debuggable
     * @param <E>  значение, реализующее Debuggable
     * @return  строковое представление мапы
     */
    public static <T extends Debuggable, E extends Debuggable> String mapToDebugString(@NotNull Map<T, E> map, boolean debugMode) {
        if (debugMode)
            return map.toString();
        if (map.size() < 1)
            return "{}";
        return '{' +
                map.entrySet().stream()
                .map(entry -> entry != null ? ((entry.getKey() != null ? entry.getKey().toString(debugMode) : "null") +
                        ":" + (entry.getValue() != null ?entry.getValue().toString(debugMode) : "null")) : "")
                .collect(Collectors.joining(", ")) +
                '}';
    }

    /**
     * Запуск всех потоков коллекции с небольшой задержкой
     * @param threads  потоки
     * @param <T>  наследник Thread
     * @param timeout  задержка между запусками потоков в мс
     */
    public static <T extends Thread> void startThreads(@NotNull Collection<T> threads, int timeout) {
        for (T thread: threads) {
            thread.start();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                logger.severe("Ошибка во время ожидания при запуске потоков: " + e.getMessage());
            }
        }
    }

    /**
     * Запуск всех потоков коллекции с задержкой по умолчанию
     * @param threads  потоки
     * @param <T>  наследник Thread
     */
    public static <T extends Thread> void startThreads(@NotNull Collection<T> threads) {
        startThreads(threads, 500);
    }

    /**
     * Ожидание завершения всех потоков коллекции
     * @param threads  потоки
     * @param <T>  наследник Thread
     */
    public static <T extends Thread> void joinThreads(@NotNull Collection<T> threads) {
        for (T thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    /**
     * Вычисление значения, если оно null
     * @param t  nullable значение
     * @param supplier  поставщик значения
     * @param <T>  тип значения
     * @return  t или значение от поставщика
     */
    public static <T> T computeIfAbsent(T t, @NotNull Supplier<T> supplier) {
        if (t != null)
            return t;
        return supplier.get();
    }

    /**
     * Получение потока по его ID
     * @param threadId  ID потока
     * @return  поток
     */
    public static Optional<Thread> getThread(long threadId) {
        return Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getId() == threadId)
                .findFirst();
    }

    /**
     * Копирует nullable-объект, реализующий интерфейс Copyable
     * @param t  nullable-объект
     * @param <T>  тип объекта
     * @return  null или копия объекта
     */
    public static <T extends Copyable<T>> T copyNullable(T t) {
        return t != null ? t.copy() : null;
    }

    /**
     * Перевод времени (не время суток!) из миллисекунд в формат "HH:MM:SS.mmm"
     * @param millis время в миллисекундах
     * @return время в нужном формате
     */
    public static String millisToHMS(long millis) {
        final int MILLIS_PER_SECOND = 1000;
        final int SECONDS_PER_MINUTE = 60;
        final int MINUTES_PER_HOUR = 60;
        int milliseconds = (int) millis % MILLIS_PER_SECOND;
        int seconds = (int) (millis / MILLIS_PER_SECOND) % SECONDS_PER_MINUTE ;
        int minutes = (int) ((millis / (MILLIS_PER_SECOND * SECONDS_PER_MINUTE)) % MINUTES_PER_HOUR);
        int hours   = (int) (millis / (MILLIS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_HOUR));
        return String.format("%d:%d:%d.%d", hours, minutes, seconds, milliseconds);
    }

    /**
     * Формирование заголовка таблицы с заданной шириной ячейки/столбца
     * @param headers  заголовки
     * @param cellWidth  ширина ячейки/столбца
     * @return  объединенные заголовки
     */
    private static String joinHeader(List<String> headers, int cellWidth) {
        StringBuilder sb = new StringBuilder();
        for (String header: headers) {
            for (int i = 0; i < cellWidth - header.length() - 1; i++)
                sb.append(" ");
            sb.append(header).append(" ");
        }
        return sb.toString();
    }
}
