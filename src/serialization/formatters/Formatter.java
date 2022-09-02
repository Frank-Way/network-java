package serialization.formatters;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Formatter {
    protected final String doubleFormat;

    public Formatter(String doubleFormat) {
        this.doubleFormat = doubleFormat;
    }

    public String getDoubleFormat() {
        return doubleFormat;
    }

    public abstract String write(String fieldName, Map<String, String> map);

    public abstract String write(String fieldName, Map<String, String> map, List<String> orderedKeys);

    public abstract String write(String fieldName, Map<String, String> map, String firstKey);

    public abstract String write(String fieldName, Collection<String> collection);

    public abstract Collection<String> readToCollection(String fieldName, String source);

    public abstract Map<String, String> readToMap(String fieldName, String source);

    public abstract String removeComments(String source);

    public abstract String appendComment(String source, String comment);

    public abstract String write(boolean value);
    public abstract String write(int value);
    public abstract String write(double value);
    public abstract String write(String value);

    public abstract boolean readBoolean(String source);
    public abstract int readInteger(String source);
    public abstract double readDouble(String source);
    public abstract String readString(String source);

    public abstract String getBooleanPattern();
    public abstract String getIntegerPattern();
    public abstract String getDoublePattern();
    public abstract String getStringPattern();

    public String getAnyPattern() {
        return "((.)|(\\s))*";
    }

    protected String getFQDNPattern() {
        return "[a-zA-Z][_a-zA-Z0-9]*(((\\.)|(\\$)).[a-zA-Z][_a-zA-Z0-9]*)*";
    }

    public abstract String getObjectPattern();
    public abstract String getEnumPattern();
    public abstract String getMapEntryPattern();

    public abstract String getCollectionPattern();


//    public abstract String write();
//    public abstract String readNull(String source);
}
