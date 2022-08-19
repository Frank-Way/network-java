package serialization.wrappers;

import serialization.formatters.Formatter;

public class StringWrapper extends SimpleWrapper{
    protected final static Class<?>[] wrappedClasses = new Class<?>[]{String.class};

    public StringWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected Object readValueInner(String yaml) {
        return yaml.substring(1, yaml.length() - 1);
    }

    @Override
    public String writeValue(Object value) {
        return '"' + value.toString() + '"';
    }

    public static boolean isString(Class<?> clazz) {
        return contains(wrappedClasses, clazz);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isString(clazz);
        if (result)
            return null;
        return "Класс не является строкой: " + clazz.getCanonicalName();
    }
}
