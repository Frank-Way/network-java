package serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static serialization.YamlSerializationOptions.CRLF;

public class YamlSerializationUtils {
    /**
     * Повторение строки заданное количество раз
     * @param string исходная строка
     * @param count количество повторений
     * @return повторённая строка
     */
    public static String repeat(String string, int count){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++)
            sb.append(string);
        return sb.toString();
    }

    public static int getIndexOfStringMatchingPattern(String[] strings, String pattern) {
        for (int row = 0; row < strings.length; row++)
            if (strings[row].matches(pattern))
                return row;
        throw new IllegalArgumentException("Не найдена строка по шаблону: " + pattern);
    }

    public static int readIntFromYaml(String yamlLine) {
        return Integer.parseInt(readContentFromYamlLine(yamlLine));
    }

    public static double readDoubleFromYaml(String yamlLine) {
        return Double.parseDouble(readContentFromYamlLine(yamlLine));
    }

    public static List<String> readListAsStringsArray(String[] yamlLines) {
        int positionOfListPrefix = yamlLines[0].indexOf(YamlSerializationOptions.YAML_LIST_PREFIX);
        List<String> result = new ArrayList<>();
        int lastRow = 0;
        for (int row = 1; row < yamlLines.length; row++)
            if (yamlLines[row].charAt(positionOfListPrefix) == '-') {
                result.add(String.join(CRLF, Arrays.copyOfRange(yamlLines, lastRow, row)));
                lastRow = row;
            }
        result.add(String.join(CRLF, Arrays.copyOfRange(yamlLines, lastRow, yamlLines.length)));
        return result;
    }

    public static String makeListInstance(String yaml) {
        if (!yaml.startsWith(repeat(YamlSerializationOptions.YAML_SPACE, YamlSerializationOptions.YAML_INDENT)))
            return YamlSerializationOptions.YAML_LIST_PREFIX + yaml;
        int spaces = 0;
        while (yaml.charAt(spaces) == YamlSerializationOptions.YAML_SPACE.charAt(0))
            spaces++;
        return repeat(YamlSerializationOptions.YAML_SPACE, spaces - YamlSerializationOptions.YAML_INDENT) + YamlSerializationOptions.YAML_LIST_PREFIX + yaml.substring(spaces);
    }

    public static String[] filterByIndent(String[] yamlLines, int startIndex) {
        String firstLine = yamlLines[startIndex];
        int indent = 0;
        while (firstLine.charAt(indent) == YamlSerializationOptions.YAML_SPACE.charAt(0))
            indent++;
        int lines = 0;
        while (startIndex + lines < yamlLines.length &&
                yamlLines[startIndex + lines].startsWith(repeat(YamlSerializationOptions.YAML_SPACE, indent)))
            lines++;
        return Arrays.copyOfRange(yamlLines, startIndex, startIndex + lines);
    }

    public static String getClassAsString(String[] yamlLines, int baseIndent) {
        String pattern = "\\s{" + baseIndent + "}(\\" + YamlSerializationOptions.YAML_LIST_PREFIX + ")?class\\" + YamlSerializationOptions.YAML_SEPARATOR + "([a-zA-Z0-9]\\.?)+";
        for (String line: yamlLines)
            if (line.matches(pattern))
                return readContentFromYamlLine(line);
        throw new IllegalArgumentException("Не найдено строки с указанием класса");
    }

    public static String doubleArrayToYaml(double[] array, String doubleFormat) {
        return '[' + Arrays.stream(array)
                .mapToObj(value -> String.format(doubleFormat, value))
                .collect(Collectors.joining("; ")) + ']';
    }

    public static double[] doubleArrayFromYaml(String yamlLine, int baseIndent) {
        String pattern = "\\s{" + baseIndent + "}" + YamlSerializationOptions.YAML_LIST_PREFIX + "\\[(\\s*-?\\d+[.,]\\d+\\s*(;\\s)?)+]\\n?";
        if (!yamlLine.matches(pattern))
            throw new IllegalArgumentException(String.format(
                    "Строка \"%s\" не соответствует шаблону \"%s\"", yamlLine, pattern));
        String[] values = yamlLine.substring(yamlLine.indexOf('[') + 1, yamlLine.lastIndexOf(']')).split(";");
        return Arrays.stream(values).map(s -> s.trim().replace(",", ".")).mapToDouble(Double::parseDouble).toArray();
    }

    public static String intArrayToYaml(int[] array) {
        return '[' + Arrays.stream(array)
                .mapToObj(value -> value + "")
                .collect(Collectors.joining("; ")) + ']';
    }

    public static int[] intArrayFromYaml(String yamlLine, int baseIndent) {
        String pattern = "\\s{" + baseIndent + "}" + YamlSerializationOptions.YAML_LIST_PREFIX + "\\[(\\s*-?\\d+\\s*(;\\s)?)+]\\n?";
        if (!yamlLine.matches(pattern))
            throw new IllegalArgumentException(String.format(
                    "Строка \"%s\" не соответствует шаблону \"%s\"", yamlLine, pattern));
        String[] values = yamlLine.trim().substring(yamlLine.indexOf('[') + 1, yamlLine.lastIndexOf(']')).split(";");
        return Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
    }

    public static String[] double2DArrayToYaml(double[][] array, String doubleFormat) {
        String[] result = new String[array.length];
        for (int row = 0; row < array.length; row++)
            result[row] = YamlSerializationOptions.YAML_LIST_PREFIX + doubleArrayToYaml(array[row], doubleFormat);
        return result;
    }

    public static double[][] double2DArrayFromYaml(String[] yamlLines, int baseIndent) {
        double[][] result = new double[yamlLines.length][];
        int cols = -1;
        for (int row = 0; row < yamlLines.length; row++) {
            result[row] = doubleArrayFromYaml(yamlLines[row], baseIndent);
            if (cols < 0)
                cols = result[row].length;
            if (cols != result[row].length)
                throw new IllegalArgumentException(String.format(
                        "В строке %d неверное количество элементов (ожидалось %d, получено %d)",
                        row, cols, result[row].length));
        }
        return result;
    }

    public static String[] int2DArrayToYaml(int[][] array) {
        String[] result = new String[array.length];
        for (int row = 0; row < array.length; row++)
            result[row] = YamlSerializationOptions.YAML_LIST_PREFIX + intArrayToYaml(array[row]);
        return result;
    }

    public static int[][] int2DArrayFromYaml(String[] yamlLines, int baseIndent) {
        int[][] result = new int[yamlLines.length][];
        for (int row = 0; row < yamlLines.length; row++)
            result[row] = intArrayFromYaml(yamlLines[row], baseIndent);
        return result;
    }

    /**
     * Добавление строки в начало каждой строки из массива строк
     * @param source массив строк
     * @param string строка, которая будет добавлена в начало всех строк массива
     * @return новый массив конкатенированных строк
     */
    public static String[] addStringAtBegin(String[] source, String string) {
        String[] result = new String[source.length];
        for (int i = 0; i < source.length; i++)
            result[i] = string + source[i];
        return result;
    }

    /**
     * Обрезает из строки заданное количество первых символов
     * @param source массив строк
     * @param count количество символов, которое необходимо обрезать с начала
     * @return новый массив обрезанных строк
     */
    public static String[] removeFirstCharacters(String[] source, int count) {
        String[] result = new String[source.length];
        for (int i = 0; i < source.length; i++)
            result[i] = source[i].substring(count);
        return result;
    }

    private static String readContentFromYamlLine(String yamlLine) {
        return yamlLine.split(YamlSerializationOptions.YAML_SEPARATOR)[1].trim();
    }
}
