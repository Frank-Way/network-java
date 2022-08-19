package serialization.wrappers;

import serialization.formatters.Formatter;

public class DoubleWrapper extends SimpleWrapper{
    protected final static Class<?>[] wrappedClasses = new Class<?>[]{double.class, Double.TYPE, Double.class};

    public DoubleWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected Object readValueInner(String yaml) {
        return Double.parseDouble(yaml.replace(",", "."));
    }

    @Override
    public String writeValue(Object value) {
        return String.format(formatter.getDoubleFormat(), value);
    }

    public static boolean isDouble(Class<?> clazz) {
        return contains(wrappedClasses, clazz);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isDouble(clazz);
        if (result)
            return null;
        return "Класс не является вещественным числом: " + clazz.getCanonicalName();
    }
}
