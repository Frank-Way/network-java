package serialization.formatters.yaml;

import serialization.YamlSerializationUtils;
import serialization.formatters.Formatter;

import java.util.*;
import java.util.stream.Collectors;

import static serialization.YamlSerializationOptions.*;
import static serialization.wrappers.complex.EnumWrapper.ENUM_CLASS_FIELD;
import static serialization.wrappers.complex.EnumWrapper.ENUM_VALUE_FIELD;
import static serialization.wrappers.complex.MapEntryWrapper.MAP_ENTRY_KEY_FIELD;
import static serialization.wrappers.complex.MapEntryWrapper.MAP_ENTRY_VALUE_FIELD;
import static serialization.wrappers.complex.ObjectWrapper.OBJECT_CLASS_FIELD;
import static serialization.wrappers.complex.collections.CollectionWrapper.COLLECTION_ITEM_FIELD;

public class YamlFormatter extends Formatter {
    public YamlFormatter(String doubleFormat) {
        super(doubleFormat);
    }

    public String write(String fieldName, Map<String, String> yaml) {
        return write(fieldName, yaml, new ArrayList<>(yaml.keySet()));
    }

    @Override
    public String write(String fieldName, Map<String, String> yaml, List<String> orderedKeys) {
        StringBuilder sb = new StringBuilder();
        if (isObjectField(fieldName))
            sb.append(CRLF);
        for (String key: orderedKeys)
            sb.append(key).append(YAML_SEPARATOR)
                    .append(YamlSerializationUtils.addIndentExcludeFirst(yaml.get(key), YAML_INDENT))
                    .append(CRLF);
        String result = YamlSerializationUtils.removeCRLFFromEnd(sb.toString());
        return result;
    }

    @Override
    public String write(String fieldName, Map<String, String> yaml, String firstKey) {
        List<String> orderedKeys = yaml.keySet().stream()
                .filter(key -> !key.equals(firstKey))
                .sorted(Comparator.comparingInt(key -> yaml.get(key).split(CRLF).length)
                        .thenComparing(Object::toString))
                .collect(Collectors.toList());
        orderedKeys.add(0, firstKey);
        return write(fieldName, yaml, orderedKeys);
    }

    public String write(String fieldName, Collection<String> yaml) {
        StringBuilder sb = new StringBuilder();
        if (isObjectField(fieldName))
            sb.append(CRLF);

        yaml.forEach(s -> sb.append(YAML_LIST_PREFIX)
                .append(YamlSerializationUtils.addIndentExcludeFirst(s, YAML_INDENT)).append(CRLF));

        String result = YamlSerializationUtils.removeCRLFFromEnd(sb.toString());
        return result;
    }

    public Collection<String> readToCollection(String fieldName, String yaml) {
        String[] lines = yaml.split(CRLF);
        if (!Arrays.stream(lines).map(s -> s.startsWith(YAML_LIST_PREFIX)).filter(b -> b).findAny().orElse(false))
            return new ArrayList<>();

        String[] rawResult = yaml.replaceFirst(YAML_LIST_PREFIX, "").split("\\n" + YAML_LIST_PREFIX);
        Collection<String> result = Arrays.stream(rawResult)
                .map(s -> YamlSerializationUtils.removeIndentExcludeFirst(s, YAML_INDENT))
                .collect(Collectors.toList());
        return result;
    }

    public Map<String, String> readToMap(String fieldName, String yaml) {
        HashMap<String, String> result = new HashMap<>();
        String[] lines = yaml.split(CRLF);
        int[] indices = YamlSerializationUtils.getIndicesOfStringsWithoutIndent(lines);
        for (int i = 0; i < indices.length - 1; i++) {
            int index = indices[i];
            int nextIndex = indices[i + 1];
            String line = lines[index];
            boolean isInnerFieldOneLiner = nextIndex - index == 1;
            if (isInnerFieldOneLiner) {
                String[] splitLine = line.split(YAML_SEPARATOR);
                result.put(YamlSerializationUtils.rtrim(splitLine[0]),
                        YamlSerializationUtils.ltrim(splitLine[1]));
            } else
                result.put(YamlSerializationUtils.rtrim(line.replace(YAML_SEPARATOR.substring(0, 1), "")),
                        YamlSerializationUtils.ltrim(String.join(CRLF, YamlSerializationUtils.removeIndent(
                                Arrays.copyOfRange(lines, index + 1, nextIndex), YAML_INDENT))));
        }
        int lastInnerFieldIndex = indices[indices.length - 1];
        boolean isLastInnerFieldOneLiner = lastInnerFieldIndex == lines.length - 1;
        String[] splitLastLine = lines[lastInnerFieldIndex].split(YAML_SEPARATOR.substring(0, 1));
        String lastInnerFieldName = splitLastLine[0];
        String lastInnerFieldValue = isLastInnerFieldOneLiner ?
                splitLastLine[1] :
                String.join(CRLF, YamlSerializationUtils.removeIndent(
                        Arrays.copyOfRange(lines, lastInnerFieldIndex + 1, lines.length), YAML_INDENT));
        result.put(YamlSerializationUtils.rtrim(lastInnerFieldName),
                YamlSerializationUtils.ltrim(lastInnerFieldValue));
        return result;
    }

    protected static boolean isObjectField(String fieldName) {
        return fieldName != null && !isCollectionItem(fieldName);
    }

//    protected static boolean isMapEntryKeyOrValue(String fieldName) {
//        return fieldName != null && (fieldName.equals(MAP_ENTRY_KEY_FIELD))
//    }

    protected static boolean isCollectionItem(String fieldName) {
        return fieldName != null && fieldName.contains(COLLECTION_ITEM_FIELD);
    }

    @Override
    public String removeComments(String yaml) {
        return yaml;
//        System.out.println(yaml);
//        return Arrays.stream(yaml.split(CRLF))
//                .map(s -> s.replace("#([^\"]|\\\\\")*$", " "))
//                .filter(s -> !s.trim().isEmpty())
//                .collect(Collectors.joining(CRLF));
    }

    @Override
    public String appendComment(String source, String comment) {
        return source + YAML_COMMENT_START + comment;
    }

    @Override
    public String write(boolean value) {
        return "'" + value + "'";
    }

    @Override
    public String write(int value) {
        return value + "";
    }

    @Override
    public String write(double value) {
        return String.format(doubleFormat, value);
    }

    @Override
    public String write(String value) {
        return '"' + value + '"';
    }

    @Override
    public boolean readBoolean(String source) {
        return Boolean.parseBoolean(source.substring(1, source.length() - 1));
    }

    @Override
    public int readInteger(String source) {
        return Integer.parseInt(source);
    }

    @Override
    public double readDouble(String source) {
        return Double.parseDouble(source.replace(",", "."));
    }

    @Override
    public String readString(String source) {
        return source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\""));
    }

    @Override
    public String getBooleanPattern() {
        return "^'((true)|(false))'$";
    }

    @Override
    public String getIntegerPattern() {
        return "^(-)?\\d+$";
    }

    @Override
    public String getDoublePattern() {
        return "^( )*(-)?\\d+[,.]\\d+$";
    }

    @Override
    public String getStringPattern() {
        return "^\".*\"$";
    }

    @Override
    public String getObjectPattern() {
        return "^(" + getObjectFieldPattern() + "\\n)*" +
                getObjectClassFieldPattern() +
                "(\\n" + getObjectFieldPattern() + ")*$";
    }

    protected String getObjectFieldPattern() {
        return YAML_IDENTIFIER_PATTERN.substring(YAML_IDENTIFIER_PATTERN.indexOf('^') + 1,
                YAML_IDENTIFIER_PATTERN.lastIndexOf('$')) + YAML_SEPARATOR + getAnyPattern();
    }

    protected String getObjectClassFieldPattern() {
        return YamlSerializationUtils.escapeDotsInPattern(OBJECT_CLASS_FIELD) + YAML_SEPARATOR + getFQDNPattern();
    }

    @Override
    public String getEnumPattern() {
        return "^((" + getEnumClassPattern() + "\\n" + getEnumValuePattern() +
                ")|(" + getEnumValuePattern() + "\\n" + getEnumClassPattern() + "))$";
    }

    protected String getEnumClassPattern() {
        return YamlSerializationUtils.escapeDotsInPattern(ENUM_CLASS_FIELD) + YAML_SEPARATOR + getFQDNPattern();
    }

    protected String getEnumValuePattern() {
        return YamlSerializationUtils.escapeDotsInPattern(ENUM_VALUE_FIELD) + YAML_SEPARATOR + "[a-zA-Z][a-zA-Z0-9]*";
    }

    @Override
    public String getMapEntryPattern() {
        return "^((" + getMapEntryKeyPattern() + "\\n" + getMapEntryValuePattern() +
                ")|(" + getMapEntryValuePattern() + "\\n" + getMapEntryKeyPattern() + "))$";
    }

    protected String getMapEntryKeyPattern() {
        return YamlSerializationUtils.escapeDotsInPattern(MAP_ENTRY_KEY_FIELD) + YAML_SEPARATOR + getAnyPattern();
    }

    protected String getMapEntryValuePattern() {
        return YamlSerializationUtils.escapeDotsInPattern(MAP_ENTRY_VALUE_FIELD) + YAML_SEPARATOR + getAnyPattern();
    }

    @Override
    public String getCollectionPattern() {
        return "^" + getCollectionItemPattern() +
                "(\\n" + getCollectionItemPattern() + ")*$";
    }

    protected String getCollectionItemPattern() {
        return YAML_LIST_PREFIX + getAnyPattern();
    }

    //    @Override
//    public String write() {
//        return "null";
//    }
//
//    @Override
//    public String readNull(String source) {
//        return null;
//    }
}
