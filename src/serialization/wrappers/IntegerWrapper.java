package serialization.wrappers;

import serialization.formatters.Formatter;

public class IntegerWrapper extends SimpleWrapper{
    protected final static Class<?>[] wrappedClasses = new Class<?>[]{int.class, Integer.TYPE, Integer.class};

    public IntegerWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected Object readValueInner(String yaml) {
        return Integer.parseInt(yaml);
    }

    @Override
    public String writeValue(Object value) {
        return value.toString();
    }

    public static boolean isInteger(Class<?> clazz) {
        return contains(wrappedClasses, clazz);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isInteger(clazz);
        if (result)
            return null;
        return "Класс не является целым числом: " + clazz.getCanonicalName();
    }
}
