package serialization.formatters.yaml;

import serialization.YamlSerializationUtils;
import serialization.formatters.Formatter;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static serialization.YamlSerializationOptions.*;

public class YamlFormatter extends Formatter {
    public YamlFormatter(String doubleFormat) {
        super(doubleFormat);
    }

    public String write(String fieldName, Map<String, String> yaml) {
        StringBuilder sb = new StringBuilder();
        if (isObjectField(fieldName))
            sb.append(CRLF);
        List<String> orderedKeys = yaml.keySet().stream()
                .filter(key -> !key.equals("class"))
                .sorted(Comparator.comparingInt(key -> yaml.get(key).split(CRLF).length))
                .collect(Collectors.toList());
        orderedKeys.add(0, "class");
        for (String key: orderedKeys)
            sb.append(key).append(YAML_SEPARATOR)
                    .append(YamlSerializationUtils.addIndentExcludeFirst(yaml.get(key), YAML_INDENT))
                    .append(CRLF);
        String result = YamlSerializationUtils.removeCRLFFromEnd(sb.toString());
        return result;
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
        if (!Utils.anyTrue(Arrays.stream(lines).map(s -> s.startsWith(YAML_LIST_PREFIX)).collect(Collectors.toSet())))
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
        return fieldName != null && !fieldName.isEmpty();
    }

    protected static boolean isCollectionItem(String fieldName) {
        return fieldName != null && fieldName.isEmpty();
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
}
