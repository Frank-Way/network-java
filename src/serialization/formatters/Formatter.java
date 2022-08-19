package serialization.formatters;

import java.util.Collection;
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

    public abstract String write(String fieldName, Collection<String> collection);

    public abstract Collection<String> readToCollection(String fieldName, String source);

    public abstract Map<String, String> readToMap(String fieldName, String source);

    public abstract String removeComments(String source);

    public abstract String appendComment(String source, String comment);
}
