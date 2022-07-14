package utils;

import models.math.Matrix;
import models.math.MatrixOperations;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Utils {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void print(String prompt,
                             RunConfiguration runConfiguration,
                             TrainResults results,
                             boolean printRequired,
                             boolean printParametersRequired,
                             boolean printTableRequired,
                             boolean printDynamicRequired) {
        if (!printRequired)
            return;
        logger.info(prompt + " (конфигурация):\n" + runConfiguration.toString(OverallConstants.DEBUG_MODE));
        logger.info(prompt + " (результаты):\n" + results.toString(OverallConstants.DEBUG_MODE));
        if (printParametersRequired) {
            logger.info(prompt + " (параметры):");
            for (int layer = 0; layer < runConfiguration.getTrainer().getNetwork().getLayers().size(); layer++) {
                logger.info("Слой " + (layer + 1));
                logger.info("Веса:\n" + runConfiguration.getTrainer().getNetwork().getLayers().get(layer).getParameter(0).valuesToString(OverallConstants.DOUBLE_FORMAT));
                logger.info("Смещения:\n" + runConfiguration.getTrainer().getNetwork().getLayers().get(layer).getParameter(1).valuesToString(OverallConstants.DOUBLE_FORMAT));
            }
        }
        if (printTableRequired)
            logger.info(prompt + " (таблица):\n" +
                    runConfiguration.toTable(OverallConstants.TABLE_PART_PERCENTS / 100.0));
        if (printDynamicRequired) {
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

    public static String buildStringTable(List<String> xHeaders,
                                          Matrix xValues,
                                          List<String> yHeaders,
                                          Matrix yValues,
                                          double part) {
        char mainSpliterator = '|';
        char separatorBase = '-';
        char separatorSpliterator = '+';
        StringBuilder headerSB = new StringBuilder("");
        StringBuilder separatorSB = new StringBuilder("");
        StringBuilder bodySB = new StringBuilder("");
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


    public static <T extends Debuggable, E extends Debuggable> String mapToDebugString(Map<T, E> map, boolean debugMode) {
        if (debugMode)
            return map.toString();
        return map.entrySet().stream()
                .map(e -> e.getKey().toString(debugMode) + ":" + e.getValue().toString(debugMode))
                .toString();
    }

    private static void joinHeader(StringBuilder sb, List<String> headers, int cellWidth) {
        for (String header: headers) {
            for (int i = 0; i < cellWidth - header.length() - 1; i++)
                sb.append(" ");
            sb.append(header).append(" ");
        }
    }
}
