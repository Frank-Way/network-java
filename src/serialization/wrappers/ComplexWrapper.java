package serialization.wrappers;

import serialization.formatters.Formatter;

public abstract class ComplexWrapper extends Wrapper{
    protected final static String CLASS_NAME_FIELD = "class";

    public ComplexWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public String writeValue(Object value) {
        return writeValue(null, value);
    }

    @Override
    protected Object readValueInner(String yaml) {
        return readValueInner(null, yaml);
    }

    protected abstract String writeValue(String fieldName, Object value);

    protected abstract Object readValueInner(String fieldName, String yaml);

}
