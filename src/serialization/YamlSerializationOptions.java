package serialization;

public class YamlSerializationOptions {
    // параметры, которые менять не надо
    public final static String CRLF = "\n";
    public final static int YAML_INDENT = 2;
    public final static String YAML_INDENT_STRING = "  ";
    public final static String YAML_LIST_PREFIX = "- ";
    public final static String YAML_SEPARATOR = ": ";
    public final static String YAML_SPACE = " ";
    public final static String YAML_COMMENT_START = "  # ";
    public final static String YAML_IDENTIFIER_PATTERN = "^[a-zA-Z_][a-zA-Z_0-9]*$";
}
