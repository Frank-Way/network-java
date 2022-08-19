package serialization.wrappers;

import serialization.formatters.Formatter;

public class BooleanWrapper extends SimpleWrapper {
    protected final static Class<?>[] wrappedClasses = new Class<?>[]{boolean.class, Boolean.TYPE, Boolean.class};

    public BooleanWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected Object readValueInner(String yaml) {
        return Boolean.parseBoolean(yaml.substring(1, yaml.length() - 1));
    }

    @Override
    public String writeValue(Object value) {
        Boolean booleanValue = (Boolean) value;
        return "'" + booleanValue.toString() + "'";
    }

    public static boolean isBoolean(Class<?> clazz) {
        return contains(wrappedClasses, clazz);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isBoolean(clazz);
        if (result)
            return null;
        return "Класс не является булевым значением: " + clazz.getCanonicalName();
    }
}
