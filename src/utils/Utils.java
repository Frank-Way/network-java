package utils;

import com.sun.istack.internal.NotNull;
import models.data.Data;
import models.exceptions.SerializationException;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.networks.Network;
import options.PrintOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Utils {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static String runConfigurationAndTrainResultsToString(String prompt,
                                                                 @NotNull RunConfiguration runConfiguration,
                                                                 @NotNull TrainResults results,
                                                                 @NotNull PrintOptions printOptions,
                                                                 boolean debugMode,
                                                                 double part,
                                                                 String doubleFormat) {
        StringBuilder sb = new StringBuilder();

        sb.append(prompt).append(" (конфигурация):\n").append(runConfiguration.toString(debugMode)).append("\n");
        sb.append(prompt).append(" (результаты):\n").append(results.toString(debugMode)).append("\n");

        if (printOptions.isParametersRequired())
            sb.append(prompt).append(" (параметры):\n")
                    .append(networkParametersToString(results.getNetwork(), doubleFormat)).append("\n");

        if (printOptions.isTableRequired())
            sb.append(prompt).append(" (таблица):\n")
                    .append(networkOutputToTable(runConfiguration.getFitParameters().getDataset().getValidData(),
                            results.getNetwork(), part, doubleFormat)).append("\n");

        if (printOptions.isDynamicRequired())
            sb.append(prompt).append(" (динамика):\n")
                    .append(trainDynamicToTable(results.getFitResults().getTestLossesMap(), doubleFormat)).append("\n");

        return sb.toString();
    }

    public static String networkParametersToString(Network network, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        for (int layer = 0; layer < network.layersCount(); layer++) {
            sb.append("Слой ").append(layer + 1).append("\n");
            sb.append("Веса:\n").append(network.getLayer(layer).getParameter(0).valuesToString(doubleFormat)).append("\n");
            sb.append("Смещения:\n").append(network.getLayer(layer).getParameter(1).valuesToString(doubleFormat)).append("\n");
        }
        return sb.toString();
    }

    public static String networkOutputToTable(Data data, Network network, double part, String doubleFormat) {
        Matrix x = data.getInputs();
        Matrix t = data.getOutputs();
        Matrix y = network.forward(x);
        Matrix e = t.sub(y).abs();
        return networkIOToStringTable(x, t, y, e, part, doubleFormat);
    }

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

    public static String buildStringTableFromHeadersAndBody(@NotNull List<String> xHeaders,
                                                            @NotNull Matrix xValues,
                                                            @NotNull List<String> yHeaders,
                                                            @NotNull Matrix yValues,
                                                            double part, String doubleFormat) {
        char mainSpliterator = '|';
        char separatorBase = '-';
        char separatorSpliterator = '+';
        StringBuilder headerSB = new StringBuilder();
        StringBuilder separatorSB = new StringBuilder();
        StringBuilder bodySB = new StringBuilder();
        String format = " " + doubleFormat + " ";
        int cellWidth = 2 + Integer.parseInt(doubleFormat.substring(
                doubleFormat.indexOf("%") + 1,
                doubleFormat.indexOf(".")));
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

    public static void save(Object object, String path, String filename) throws SerializationException {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            logger.severe("Ошибка при создании папки для сохранения нейросетей: " + e.getMessage());
            throw new SerializationException(e.getMessage(), e);
        }

        String fullPath = path + File.separator + filename;

        try (FileOutputStream fos = new FileOutputStream(fullPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(object);
        } catch (IOException e) {
            logger.severe("Ошибка при сохранении: " + e.getMessage());
            throw new SerializationException(e.getMessage(), e);
        }
        logger.fine("Сохранена нейросеть в файл: " + fullPath);
    }

    public static Object load(String path, String filename) throws SerializationException {
        String fullPath = path + File.separator + filename;
        try (FileInputStream fis = new FileInputStream(fullPath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Ошибка при загрузке: " + e.getMessage());
            throw new SerializationException(e.getMessage(), e);
        }
    }

    private static void joinHeader(StringBuilder sb, List<String> headers, int cellWidth) {
        for (String header: headers) {
            for (int i = 0; i < cellWidth - header.length() - 1; i++)
                sb.append(" ");
            sb.append(header).append(" ");
        }
    }
}
