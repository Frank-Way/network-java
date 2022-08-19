package utils;

import models.data.Data;
import models.math.Matrix;
import models.math.MatrixUtils;
import models.networks.Network;
import models.operations.BiasAdd;
import models.operations.WeightMultiply;
import models.trainers.FitResults;
import options.PrintOptions;
import utils.automatization.RunConfiguration;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Утилитарные методы, используемые в различных частях проекта
 */
public abstract class Utils {
    private static final transient Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
    public static String runConfigurationAndFitResultsToString(RunConfiguration runConfiguration,
                                                               FitResults results,
                                                               PrintOptions printOptions,
                                                               boolean debugMode,
                                                               String doubleFormat) {
        StringBuilder sb = new StringBuilder();

        sb.append("конфигурация:\n").append(runConfiguration.toString()).append("\n");
        sb.append("результаты:\n").append(results.toString()).append("\n");

        if (printOptions.isParametersRequired())
            sb.append("параметры:\n")
                    .append(networkParametersToString(results.getNetwork(), doubleFormat)).append("\n");

        if (printOptions.isTableRequired())
            sb.append("таблица:\n")
                    .append(networkOutputToTable(results.getDataset().getValidData(),
                            results.getNetwork(), printOptions.getTablePart(), doubleFormat)).append("\n");

        if (printOptions.isDynamicRequired())
            sb.append("динамика:\n")
                    .append(trainDynamicToTable(results.getTestLossesMap(), doubleFormat)).append("\n");

        sb.append("обучение заняло: ").append(millisToHMS(results.getTimeSpent())).append("\n");

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
    public static String networkIOToStringTable(Matrix inputs,
                                                Matrix targets,
                                                Matrix predictions,
                                                Matrix errors,
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
    public static String buildStringTableFromHeadersAndBody(List<String> xHeaders,
                                                            Matrix xValues,
                                                            List<String> yHeaders,
                                                            Matrix yValues,
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
        int[] indices = MatrixUtils.getLinSpace(0, xValues.getRows() - 1, (int)Math.round(xValues.getRows() * part));
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
     * Вычисление значения, если оно null
     * @param nullableValue  nullable значение
     * @param supplier  поставщик значения
     * @param <T>  тип значения
     * @return  t или значение от поставщика
     */
    public static <T> T computeIfAbsent(T nullableValue, Supplier<T> supplier) {
        if (nullableValue != null)
            return nullableValue;
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

    public static void myWait(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            logger.severe("Ошибка во время ожидания: " + e.getMessage());
        }
    }

    public static boolean allTrue(Collection<Boolean> booleans) {
        return !booleans.contains(false);
    }

    public static boolean anyTrue(Collection<Boolean> booleans) {
        return booleans.contains(true);
    }

    protected static <T> T reduceCollection(Collection<T> collection, BinaryOperator<T> accumulator, T defaultValue) {
        return collection.stream().reduce(accumulator).orElse(defaultValue);
    }

    public static <T> boolean listsDeepEquals(List<T> list1, List<T> list2) {
        return list1.size() == list2.size() && allTrue(IntStream.range(0, list1.size())
                .mapToObj(i -> list1.get(i).equals(list2.get(i)))
                .collect(Collectors.toSet()));
    }

    public static <T> T computeOrGetNull(T nullableValue, Supplier<T> supplier) {
        if (nullableValue == null)
            return null;
        return supplier.get();
    }

    public static <T> boolean contains(T[] array, T value) {
        boolean result = false;
        int i = 0;
        while (!result || i++ < array.length)
            result = array[i].equals(value);
        return result;
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
