package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.Matrix;
import models.math.MatrixOperations;
import options.OverallConstants;
import options.PrintOptions;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Utils {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void print(String prompt,
                             @NotNull RunConfiguration runConfiguration,
                             @NotNull TrainResults results,
                             boolean printRequired,
                             @NotNull PrintOptions printOptions) {
        if (!printRequired)
            return;
        logger.info(prompt + " (конфигурация):\n" + runConfiguration.toString(OverallConstants.DEBUG_MODE));
        logger.info(prompt + " (результаты):\n" + results.toString(OverallConstants.DEBUG_MODE));
        if (printOptions.isParametersRequired()) {
            logger.info(prompt + " (параметры):");
            for (int layer = 0; layer < runConfiguration.getTrainer().getNetwork().layersCount(); layer++) {
                logger.info("Слой " + (layer + 1));
                logger.info("Веса:\n" + runConfiguration.getTrainer().getNetwork().getLayer(layer).getParameter(0).valuesToString(OverallConstants.DOUBLE_FORMAT));
                logger.info("Смещения:\n" + runConfiguration.getTrainer().getNetwork().getLayer(layer).getParameter(1).valuesToString(OverallConstants.DOUBLE_FORMAT));
            }
        }
        if (printOptions.isTableRequired())
            logger.info(prompt + " (таблица):\n" +
                    runConfiguration.toTable(OverallConstants.TABLE_PART_PERCENTS / 100.0));
        if (printOptions.isDynamicRequired()) {
            List<String> xHeaders = Collections.singletonList("эпоха");
            List<String> yHeaders = Collections.singletonList("потеря");
            int rows = results.getFitResults().getTestLossesMap().size();
            List<Double> tmpList1 = results.getFitResults().getTestLossesMap().keySet().stream().sorted().map(Integer::doubleValue).collect(Collectors.toList());
            List<Double> tmpList2 = tmpList1.stream().map(d -> results.getFitResults().getTestLossesMap().get(d.intValue())).collect(Collectors.toList());
            double[][] xValuesDA = new double[rows][1];
            double[][] yValuesDA = new double[rows][1];
            for (int row = 0; row < rows; row++) {
                xValuesDA[row][0] = tmpList1.get(row);
                yValuesDA[row][0] = tmpList2.get(row);
            }
            Matrix xValues = new Matrix(xValuesDA);
            Matrix yValues = new Matrix(yValuesDA);
            logger.info(prompt + " (динамика):\n" + buildStringTable(xHeaders, xValues, yHeaders, yValues, 1.0));
        }
    }

    public static String buildStringTable(@NotNull List<String> xHeaders,
                                          @NotNull Matrix xValues,
                                          @NotNull List<String> yHeaders,
                                          @NotNull Matrix yValues,
                                          double part) {
        char mainSpliterator = '|';
        char separatorBase = '-';
        char separatorSpliterator = '+';
        StringBuilder headerSB = new StringBuilder();
        StringBuilder separatorSB = new StringBuilder();
        StringBuilder bodySB = new StringBuilder();
        String format = " " + OverallConstants.DOUBLE_FORMAT + " ";
        int cellWidth = 2 + Integer.parseInt(OverallConstants.DOUBLE_FORMAT.substring(
                OverallConstants.DOUBLE_FORMAT.indexOf("%") + 1,
                OverallConstants.DOUBLE_FORMAT.indexOf(".")));
        joinHeader(headerSB, xHeaders, cellWidth);
        headerSB.append(mainSpliterator);
        joinHeader(headerSB, yHeaders, cellWidth);
        for (int i = 0; i < headerSB.toString().indexOf(mainSpliterator); i++)
            separatorSB.append(separatorBase);
        separatorSB.append(separatorSpliterator);
        while (separatorSB.length() < headerSB.length())
            separatorSB.append(separatorBase);
        int[] indices = MatrixOperations.getLinSpace(0, xValues.getRows() - 1, (int)Math.round(xValues.getRows() * part));
        for (int row: indices) {
            for (int col = 0; col < xValues.getCols(); col++)
                bodySB.append(String.format(format, xValues.getValues()[row][col]));
            bodySB.append(mainSpliterator);
            for (int col = 0; col < yValues.getCols(); col++)
                bodySB.append(String.format(format, yValues.getValues()[row][col]));
            bodySB.append("\n");
        }
        return String.join("\n", headerSB.toString(), separatorSB.toString(), bodySB.toString());
    }

    public static <T extends Debuggable, E extends Debuggable> String mapToDebugString(@NotNull Map<T, E> map, boolean debugMode) {
        if (debugMode)
            return map.toString();
        return map.entrySet().stream()
                .map(entry -> entry.getKey().toString(debugMode) + ":" + entry.getValue().toString(debugMode))
                .toString();
    }

    public static <T extends Thread> void joinThreads(@NotNull Collection<T> threads) {
        for (T thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public static <T> T computeIfAbsent(T t, @NotNull Supplier<T> supplier) {
        if (t != null)
            return t;
        return supplier.get();
    }

    public static Optional<Thread> getThread(long threadId) {
        return Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getId() == threadId)
                .findFirst();
    }

    public static <T extends Copyable<T>> T copyNullable(T t) {
        return t != null ? t.copy() : null;
    }

    private static void joinHeader(StringBuilder sb, List<String> headers, int cellWidth) {
        for (String header: headers) {
            for (int i = 0; i < cellWidth - header.length() - 1; i++)
                sb.append(" ");
            sb.append(header).append(" ");
        }
    }
}
