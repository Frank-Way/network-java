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
     * @param runConfiguration конфигурация обучения
     * @param results          результаты обучения
     * @param printOptions     опции вывода
     * @param debugMode        режим вывода
     * @param doubleFormat     формат вывода вещественных чисел
     * @return                 строка с требуемыми значениями
     */
    public static String runConfigurationAndFitResultsToString(RunConfiguration runConfiguration,
                                                               FitResults results,
                                                               PrintOptions printOptions,
                                                               boolean debugMode,
                                                               String doubleFormat) {
        final StringBuilder sb = new StringBuilder();

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

        sb.append("обучение заняло: ").append(millisToHMS(results.getTimeStop() - results.getTimeStart())).append("\n");

        return sb.toString();
    }

    /**
     * Формирование строки с параметрами сети по слоям
     * @param network      сеть
     * @param doubleFormat формат вывода вещественных чисел
     * @return             строка с описанием параметров сети
     */
    public static String networkParametersToString(Network network, String doubleFormat) {
        final StringBuilder sb = new StringBuilder();
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
     * @param data         выборка для проверки
     * @param network      сеть
     * @param part         какую часть таблицы необходимо вывести
     * @param doubleFormat формат вывода вещественных чисел
     * @return             строка с таблицей
     */
    public static String networkOutputToTable(Data data, Network network, double part, String doubleFormat) {
        final Matrix x = data.getInputs();
        final Matrix t = data.getOutputs();
        final Matrix y = network.forward(x);
        final Matrix e = t.sub(y).abs();
        return networkIOToStringTable(x, t, y, e, part, doubleFormat);
    }

    /**
     * Формирование таблицы зависимости потери от эпохи
     * @param map          мапа, где ключ - номер эпохи, а значение - потеря
     * @param doubleFormat формат вывода вещественных чисел
     * @return             строка с таблицей
     */
    public static String trainDynamicToTable(Map<Integer, Double> map, String doubleFormat) {
        final List<String> leftHeaders = Collections.singletonList("эпоха");
        final List<String> rightHeaders = Collections.singletonList("потеря");
        final List<List<String>> leftColumns = new ArrayList<>();
        leftColumns.add(new ArrayList<>());
        final List<List<String>> rightColumns = new ArrayList<>();
        rightColumns.add(new ArrayList<>());
        final int rows = map.size();
        List<Integer> epochs = map.keySet().stream().sorted().collect(Collectors.toList());
        for (int row = 0; row < epochs.size(); row++) {
            leftColumns.get(0).add(epochs.get(row) + "");
            rightColumns.get(0).add(String.format(doubleFormat, map.get(epochs.get(row))));
        }
        return buildStringTableFromHeadersAndBody(leftHeaders, leftColumns, rightHeaders, rightColumns);
    }

    /**
     * Формирование таблицы с результатами работы сети
     * @param inputs       входные значения
     * @param targets      требуемые выходные значения
     * @param predictions  результаты вычислений сети
     * @param errors       ошибки вычисления сети
     * @param part         какую часть таблицы необходимо вывести
     * @param doubleFormat формат вывода вещественных чисел
     * @return             строка с таблицей
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
     * @param xHeaders     заголовки агрументов
     * @param xValues      аргументы
     * @param yHeaders     заголовки значений
     * @param yValues      значения
     * @param part         какую часть таблицы необходимо вывести
     * @param doubleFormat формат вывода вещественных чисел
     * @return             строка с таблицей
     */
    public static String buildStringTableFromHeadersAndBody(List<String> xHeaders,
                                                            Matrix xValues,
                                                            List<String> yHeaders,
                                                            Matrix yValues,
                                                            double part, String doubleFormat) {
        final List<String> leftHeaders = new ArrayList<>(xHeaders);
        final List<List<String>> leftColumns = IntStream.range(0, xValues.getCols()).mapToObj(i -> new ArrayList<String>()).collect(Collectors.toList());
        final List<String> rightHeaders = new ArrayList<>(yHeaders);
        final List<List<String>> rightColumns = IntStream.range(0, yValues.getCols()).mapToObj(i -> new ArrayList<String>()).collect(Collectors.toList());

        int[] indices = MatrixUtils.getLinSpace(0, xValues.getRows() - 1, (int)Math.round(xValues.getRows() * part));
        for (int row: indices) {
            for (int col = 0; col < xValues.getCols(); col++)
                leftColumns.get(col).add(String.format(doubleFormat, xValues.getValue(row, col)).trim());
            for (int col = 0; col < yValues.getCols(); col++)
                rightColumns.get(col).add(String.format(doubleFormat, yValues.getValue(row, col)).trim());
        }

        return buildStringTableFromHeadersAndBody(leftHeaders, leftColumns, rightHeaders, rightColumns);
    }

    /**
     * Формирование таблицы по заголовкам и телу
     * @param leftHeaders  левые заголовки
     * @param leftColumns  левые значения
     * @param rightHeaders правые заголовки
     * @param rightColumns правые значения
     * @return             строка с таблицей
     */
    public static String buildStringTableFromHeadersAndBody(List<String> leftHeaders,
                                                            List<List<String>> leftColumns,
                                                            List<String> rightHeaders,
                                                            List<List<String>> rightColumns) {
        final char leftRightSpliterator = '|';
        final char upDownSpliterator = '-';
        final char spliteratorsIntersection = '+';
        final char spaceFiller = ' ';

        final StringBuilder headerSB = new StringBuilder();
        final StringBuilder separatorSB = new StringBuilder();
        final StringBuilder bodySB = new StringBuilder();

        final List<String> combinedHeaders = new ArrayList<>(leftHeaders);
        final List<List<String>> combinedColumns = new ArrayList<>(leftColumns);
        combinedHeaders.addAll(rightHeaders);
        combinedColumns.addAll(rightColumns);

        final int[] cellWidths = calcCellWidths(combinedHeaders, combinedColumns);

        headerSB.append(buildRow(leftHeaders, rightHeaders, cellWidths, spaceFiller, leftRightSpliterator, 1, 1));

        for (int i = 0; i < headerSB.toString().indexOf(leftRightSpliterator); i++)
            separatorSB.append(upDownSpliterator);
        separatorSB.append(spliteratorsIntersection);
        while (separatorSB.length() < headerSB.length())
            separatorSB.append(upDownSpliterator);

        for (int row = 0; row < leftColumns.get(0).size(); row++) {
            int finalRow = row;
            bodySB.append(buildRow(
                    leftColumns.stream().map(leftColumn -> leftColumn.get(finalRow)).collect(Collectors.toList()),
                    rightColumns.stream().map(leftColumn -> leftColumn.get(finalRow)).collect(Collectors.toList()),
                    cellWidths, spaceFiller, leftRightSpliterator, 1, 1
            )).append("\n");
        }
        return String.join("\n", headerSB.toString(), separatorSB.toString(), bodySB.toString());
    }

    /**
     * Вычисление ширины столбца, как максимальной ширины заголовка или значений столбца
     * @param headers заголовки
     * @param columns значения столбцов
     * @return        набор максимальных длин значений столбцов
     */
    private static int[] calcCellWidths(List<String> headers,
                                        List<List<String>> columns) {
        return IntStream.range(0, headers.size())                              // перебираем столбцы
                .map(i -> Math.max(headers.get(i).length(),                    // берем максимум из заголовка или столбца
                        columns.get(i).stream()                                // перебираем значения из столбца
                                .mapToInt(String::length)                      // из строк берем длины
                                .max()                                         // берем максимальное значение
                                .orElseThrow(IllegalArgumentException::new)))  // иначе пробрасываем ошибку
                .toArray();                                                    // формируем массив
    }

    /**
     * Формирование строки
     * @param leftStrings          значения до разделителя
     * @param rightStrings         значения после разделителя
     * @param cellWidths           ширины столбцов
     * @param spaceFiller          заполнитель пустого пространства
     * @param leftRightSpliterator разделитель левой и правой части столбца
     * @param leftPadding          буфер слева
     * @param rightPadding         буфер справа
     * @return                     строка
     */
    private static String buildRow(List<String> leftStrings, List<String> rightStrings, int[] cellWidths,
                                   char spaceFiller, char leftRightSpliterator, int leftPadding, int rightPadding) {
        final StringBuilder sb = new StringBuilder();
        int cnt = 0;  // количество обработанных значений
        for (String string: leftStrings)
            sb.append(buildCell(string, cellWidths[cnt++], leftPadding, rightPadding, spaceFiller));
        sb.append(leftRightSpliterator);
        for (String string: rightStrings)
            sb.append(buildCell(string, cellWidths[cnt++], leftPadding, rightPadding, spaceFiller));
        return sb.toString();
    }

    /**
     * Формирование ячейки строки
     * @param value        значение
     * @param cellWidth    ширина ячейки
     * @param leftPadding  буфер слева
     * @param rightPadding буфер справа
     * @param spaceFiller  заполнитель пустого пространства
     * @return             ячейка
     */
    private static String buildCell(String value, int cellWidth, int leftPadding, int rightPadding, char spaceFiller) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPadding; i++)
            sb.append(spaceFiller);
        for (int i = 0; i < cellWidth - value.length(); i++)
            sb.append(spaceFiller);
        sb.append(value);
        for (int i = 0; i < rightPadding; i++)
            sb.append(spaceFiller);
        return sb.toString();
    }

    /**
     * Получение потока по его ID
     * @param threadId ID потока
     * @return         поток
     */
    public static Optional<Thread> getThread(long threadId) {
        return Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getId() == threadId)
                .findFirst();
    }

    /**
     * Перевод времени (не время суток!) из миллисекунд в формат "HH:MM:SS.mmm"
     * @param millis время в миллисекундах
     * @return       время в нужном формате
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
     * Ожидание обертка Thread.sleep() с try-catch
     * @param timeout величина таймаута
     */
    public static void myWait(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            logger.severe("Ошибка во время ожидания: " + e.getMessage());
        }
    }
}
