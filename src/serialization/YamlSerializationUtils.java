package serialization;

import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import serialization.exceptions.SerializationException;
import serialization.formatters.yaml.YamlFormatter;
import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static serialization.YamlSerializationOptions.*;

public class YamlSerializationUtils {
    private static final transient Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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

    public static String readStringFromYaml(String yamlLine) {
        return readContentFromYamlLine(yamlLine);
    }

    public static List<String> readListAsStringsArray(String[] yamlLines) {
        int positionOfListPrefix = yamlLines[0].indexOf(YAML_LIST_PREFIX);
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
        if (!yaml.startsWith(repeat(YAML_SPACE, YAML_INDENT)))
            return YAML_LIST_PREFIX + yaml;
        int spaces = 0;
        while (yaml.charAt(spaces) == YAML_SPACE.charAt(0))
            spaces++;
        return repeat(YAML_SPACE, spaces - YAML_INDENT) + YAML_LIST_PREFIX + yaml.substring(spaces);
    }

    public static int getIndent(String line) {
        int indent = 0;
        while (line.charAt(indent) == YAML_SPACE.charAt(0))
            indent++;
        return indent;
    }

    public static String[] filterByIndent(String[] yamlLines, int startIndex) {
        String firstLine = yamlLines[startIndex];
        int indent = getIndent(firstLine);
        while (firstLine.charAt(indent) == YAML_SPACE.charAt(0))
            indent++;
        int lines = 0;
        while (startIndex + lines < yamlLines.length &&
                yamlLines[startIndex + lines].startsWith(repeat(YAML_SPACE, indent)))
            lines++;
        return Arrays.copyOfRange(yamlLines, startIndex, startIndex + lines);
    }

    public static String[] filterByIndent2(String[] yamlLines, int indent) {
        final List<Integer> indices = new ArrayList<>();
        final String pattern = "\\s{" + indent + "}[^ ].*";
        for (int i = 0; i < yamlLines.length; i++) {
            if (yamlLines[i].matches(pattern))
                indices.add(i);
        }
        final String[] result = new String[indices.size()];
        int i = 0;
        for (Integer index: indices)
            result[i++] = yamlLines[index];
        return result;
    }

    public static String[] filterByIndent3(String[] yamlLines, int startIndex) {
        final List<Integer> indices = new ArrayList<>();
        final int indent = getIndent(yamlLines[startIndex]);
        int currentIndent;
        for (int lineNumber = startIndex; lineNumber < yamlLines.length; lineNumber++) {
            currentIndent = getIndent(yamlLines[lineNumber]);
            if (currentIndent < indent)
                break;
            if (currentIndent == indent)
                indices.add(lineNumber);
        }
        final String[] result = new String[indices.size()];
        int i = 0;
        for (Integer index: indices)
            result[i++] = yamlLines[index];
        return result;
    }

    public static String[] extractMultiplyLinesYaml(String[] lines, String firstLine) {
        int startIndex = -1;
        for (int i = 0; i < lines.length; i++)
            if (lines[i].equals(firstLine)) {
                startIndex = i;
                break;
            }
        if (startIndex < 0)
            throw new IllegalArgumentException("Не найдена строка: " + firstLine);
        String[] content = filterByIndent(lines, startIndex + 1);
        String[] result = new String[content.length + 1];
        result[0] = firstLine;
        System.arraycopy(content, 0, result, 1, content.length);
        return result;
    }

    public static String getClassAsString(String[] yamlLines, int baseIndent) {
        String pattern = "\\s{" + baseIndent + "}(\\" + YAML_LIST_PREFIX + ")?class\\" + YAML_SEPARATOR + "([a-zA-Z0-9]\\.?)+";
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
        String pattern = "\\s{" + baseIndent + "}" + YAML_LIST_PREFIX + "\\[(\\s*-?\\d+[.,]\\d+\\s*(;\\s)?)+]\\n?";
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
        String pattern = "\\s{" + baseIndent + "}" + YAML_LIST_PREFIX + "\\[(\\s*-?\\d+\\s*(;\\s)?)+]\\n?";
        if (!yamlLine.matches(pattern))
            throw new IllegalArgumentException(String.format(
                    "Строка \"%s\" не соответствует шаблону \"%s\"", yamlLine, pattern));
        String[] values = yamlLine.trim().substring(yamlLine.indexOf('[') + 1, yamlLine.lastIndexOf(']')).split(";");
        return Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
    }

    public static String[] double2DArrayToYaml(double[][] array, String doubleFormat) {
        String[] result = new String[array.length];
        for (int row = 0; row < array.length; row++)
            result[row] = YAML_LIST_PREFIX + doubleArrayToYaml(array[row], doubleFormat);
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
            result[row] = YAML_LIST_PREFIX + intArrayToYaml(array[row]);
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
        return Arrays.stream(source).map(s -> s.substring(count)).toArray(String[]::new);
    }

    /**
     * Проверка на то, что все строки (объединение по И) соответствуют хотя бы одному шаблону (объединение по ИЛИ)
     * @param strings строки
     * @param patterns шаблоны
     * @return результат проверки
     */
    public static boolean allStringsMatchAnyPattern(String[] strings, String[] patterns) {
        return Arrays.stream(patterns)
                .map(pattern -> Arrays.stream(strings)
                        .map(line -> line.matches(pattern))
                        .reduce((b1, b2) -> b1 || b2)
                        .orElse(false))
                .reduce((b1, b2) -> b1 && b2)
                .orElse(false);
    }

    public static String removeCRLFFromBegin(String string) {
        String result = new String(string);
        while (result.startsWith(CRLF))
            result = result.substring(1);
        return result;
    }

    public static String removeCRLFFromEnd(String string) {
        String result = new String(string);
        while (result.endsWith(CRLF))
            result = result.substring(0, result.length() - 1);
        return result;
    }

    public static String trimCRLF(String string) {
        return removeCRLFFromEnd(removeCRLFFromBegin(string));
    }

    public static Collection<String> classesStreamToClassNames(Stream<Class<?>> classes) {
        return classes.map(Class::getCanonicalName).collect(Collectors.toSet());
    }

    public static boolean classesStreamContainsClass(Stream<Class<?>> classes, String canonicalClassName) {
        return classes.map(Class::getCanonicalName).filter(cls -> cls.equals(canonicalClassName))
                .findAny().orElse(null) != null;
    }

    private static String readContentFromYamlLine(String yamlLine) {
        return yamlLine.split(YAML_SEPARATOR)[1].trim();
    }

    public static String getNamePattern() {
        return "[a-zA-Z][a-zA-Z0-9]*";
    }

    public static String getOptionalNamePattern() {
        return makePatternOptional(getNamePatternWithSeparator());
    }

    public static String getNamePatternWithSeparator() {
        return getNamePattern() + YAML_SEPARATOR;
    }

    public static String makePatternOptional(String pattern) {
        return "(" + pattern + ")?";
    }

    public static String getMultipleLinesPattern() {
        return getOptionalNamePattern();
    }

    public static String makePatternMatchingFullString(String pattern) {
        return "^" + pattern + "$";
    }

    public static String getIndentPattern(int indent) {
        return " {" + indent + "}";
    }

    public static String addQuantifierToPattern(String pattern, String quantifier) {
        return "(" + pattern + ")" + quantifier;
    }

    public static String addQuantifierToPattern(String pattern, int quantifier) {
        return "(" + pattern + "){" + quantifier + "}";
    }

    public static String addQuantifierToPattern(String pattern, int quantifierFrom, int quantifierTo) {
        return "(" + pattern + "){" + quantifierFrom + "," + quantifierTo + "}";
    }

    public static String escapeDotsInPattern(String pattern) {
        return pattern.replaceAll("\\.", "\\\\.");
    }

    public static String convertFieldToGetterMethodName(Field field) {
        return "get" + makeFirstCharUpper(field.getName());
    }

    public static String makeFirstCharUpper(String string) {
        return string.substring(1) + Character.toUpperCase(string.charAt(0));
    }

    public static Field[] combine(Field[] ... arrays) {
        return Arrays.stream(arrays).flatMap(Arrays::stream).toArray(Field[]::new);
    }

    public static String addIndent(String source, int indent) {
        return String.join(CRLF, addStringAtBegin(source.split(CRLF), repeat(YAML_SPACE, indent)));
    }

    public static String[] addIndent(String[] source, int indent) {
        return addStringAtBegin(source, repeat(YAML_SPACE, indent));
    }

    public static String addIndentExcludeFirst(String source, int indent) {
        String[] lines = source.split(CRLF);
        String[] firstLine = new String[]{lines[0]};
        String[] otherLines = Arrays.copyOfRange(lines, 1, lines.length);
        String[] otherLinesWithIndent = addIndent(otherLines, indent);
        return String.join(CRLF, Stream.of(firstLine, otherLinesWithIndent).flatMap(Arrays::stream).toArray(String[]::new));
    }

    public static String removeIndent(String source, int indent) {
        return String.join(CRLF, Arrays.stream(source.split(CRLF))
                .map(s -> s.substring(indent)).toArray(String[]::new));
    }

    public static String[] removeIndent(String[] source, int indent) {
        return removeFirstCharacters(source, indent);
    }

    public static String removeIndentReplacingFirst(String source, int indent) {
        String[] lines = source.split(CRLF);
        String[] firstLine = new String[]{lines[0]};
        int firstLineSeparatorPosition = firstLine[0].indexOf(YAML_SEPARATOR);
        int firstLinePrefixPosition = firstLine[0].indexOf(YAML_LIST_PREFIX);
        String indentString = YamlSerializationUtils.repeat(YAML_SPACE, indent);
        if (firstLineSeparatorPosition >= 0) {

        } else if (firstLinePrefixPosition >= 0) {

        } else if (firstLine[0].startsWith(indentString)) {

        }
        String[] otherLines = Arrays.copyOfRange(lines, 1, lines.length);
        String[] otherLinesWithoutIndent = removeIndent(otherLines, indent);
        return String.join(CRLF, Stream.of(firstLine, otherLinesWithoutIndent).flatMap(Arrays::stream).toArray(String[]::new));
    }

    public static String removeIndentExcludeFirst(String source, int indent) {
        String[] lines = source.split(CRLF);
        String[] firstLine = new String[]{lines[0]};
        String[] otherLines = Arrays.copyOfRange(lines, 1, lines.length);
        String[] otherLinesWithoutIndent = removeIndent(otherLines, indent);
        return String.join(CRLF, Stream.of(firstLine, otherLinesWithoutIndent).flatMap(Arrays::stream).toArray(String[]::new));
    }

    public static String[] removeIndentExcludeFirst(String[] source, int indent) {
        String[] firstLine = new String[]{source[0]};
        String[] otherLines = Arrays.copyOfRange(source, 1, source.length);
        String[] otherLinesWithoutIndent = removeIndent(otherLines, indent);
        return Stream.of(firstLine, otherLinesWithoutIndent).flatMap(Arrays::stream).toArray(String[]::new);
    }

    public static int[] getIndicesOfStringsWithoutIndent(String[] lines) {
        return IntStream.range(0, lines.length).filter(i -> !lines[i].startsWith(YAML_INDENT_STRING)).toArray();
    }

    public static boolean yamlFieldsEquals(Object object1, Object object2) {
        Class<?> class1 = object1.getClass();
        Class<?> class2 = object2.getClass();
        if (!class1.equals(class2))
            return false;

        boolean delegateValidation = class1.getAnnotation(YamlSerializable.class).delegateValidationToEquals();
        if (delegateValidation)
            return object1.equals(object2);

        Field[] yamlFields = getYamlFields(class1);
        boolean result = true;

        for (int i = 0; result && i < yamlFields.length; i++) {
            Field yamlField = yamlFields[i];
            yamlField.setAccessible(true);
            try {
                Object value1 = yamlField.get(object1);
                Object value2 = yamlField.get(object2);
                if (value1.getClass().isArray()) {
                    if (value1.getClass().getComponentType().isAnnotationPresent(YamlSerializable.class))
                        result = result && yamlFieldsEquals((Object[]) value1, (Object[]) value2);
                    else {
                        if (value1.getClass().getComponentType().isPrimitive()) {
                            if (value1.getClass().getComponentType().equals(int.class))
                                result = result && Arrays.equals((int[]) value1, (int[]) value2);
                            else if (value1.getClass().getComponentType().equals(double.class))
                                result = result && Arrays.equals((double[]) value1, (double[]) value2);
                            else if (value1.getClass().getComponentType().equals(boolean.class))
                                result = result && Arrays.equals((boolean[]) value1, (boolean[]) value2);
                        } else
                            result = result && Arrays.deepEquals((Object[]) value1, (Object[]) value2);
                    }
                }
                else {
                    if (yamlField.getType().isAnnotationPresent(YamlSerializable.class))
                        result = result && yamlFieldsEquals(value1, value2);
                    else
                        result = result && Objects.equals(value1, value2);
                }
            } catch (IllegalAccessException e) {
                logger.severe("Ошибка при доступе к полям объекта: " + e.getMessage());
                result = false;
            }
        }
        return result;
    }

    public static boolean yamlFieldsEquals(Object[] array1, Object[] array2) {
        if (!(array1.getClass().isArray() && array2.getClass().isArray()))
            return false;
        int size1 = Array.getLength(array1);
        int size2 = Array.getLength(array2);
        if (size1 != size2)
            return false;
        return !IntStream.range(0, size1)
                .mapToObj(i -> yamlFieldsEquals(Array.get(array1, i), Array.get(array2, i)))
                .collect(Collectors.toSet()).contains(false);
    }

    public static boolean yamlFieldsDeepEquals(Object array1, Object array2) {
        return yamlFieldsEquals((Object[]) array1, (Object[]) array2);
    }

    public static Field[] getYamlFields(Class<?> clazz) {
        Field[] fields = SerializationUtils.getAllFields(clazz);
        Field[] result = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(YamlField.class))
                .toArray(Field[]::new);
        Field fieldWithIncorrectName = Arrays.stream(result)
                .filter(field ->  {
                    String specifiedFieldName = field.getAnnotation(YamlField.class).fieldName();
                    if (!specifiedFieldName.isEmpty())
                        return !specifiedFieldName.matches(YAML_IDENTIFIER_PATTERN);
                    return false;
                })
                .findAny().orElse(null);
        if (fieldWithIncorrectName != null) {
            String specifiedFieldName = fieldWithIncorrectName.getAnnotation(YamlField.class).fieldName();
            throw new IllegalArgumentException(String.format("Поле \"%s\" имеет недопустимый Yaml-идентификатор \"%s\"",
                    fieldWithIncorrectName.getName(), specifiedFieldName));
        }
        return result;
    }

    public static String ltrim(String source) {
        return source.replaceAll("^\\s+", "");
    }

    public static String rtrim(String source) {
        return source.replaceAll("\\s+$", "");
    }

    public static boolean isEqualsYamlSerializable(Object object1, Object object2) {
        boolean result = false;
        Class<?> class1 = object1.getClass();
        Class<?> class2 = object2.getClass();
        if (!class1.equals(class2))
            return result;
        Formatter formatter = new YamlFormatter("%10.5f");
        Wrapper wrapper1 = WrapperFactory.createWrapper(class1, formatter);
        Wrapper wrapper2 = WrapperFactory.createWrapper(class2, formatter);
        String serialized1 = wrapper1.writeValue(object1);
        String serialized2 = wrapper2.writeValue(object2);
        String[] lines1 = serialized1.split(CRLF);
        String[] lines2 = serialized2.split(CRLF);
        Set<String> set1 = Arrays.stream(lines1).collect(Collectors.toSet());
        Set<String> set2 = Arrays.stream(lines2).collect(Collectors.toSet());
        set1.removeAll(set2);
        return set1.isEmpty();
    }
}
