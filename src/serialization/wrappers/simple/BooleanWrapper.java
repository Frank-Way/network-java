package serialization.wrappers.simple;

import serialization.formatters.Formatter;

public class BooleanWrapper extends SimpleWrapper {
    public BooleanWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValue(String yaml) {
        return formatter.readBoolean(yaml);
    }

    @Override
    public String writeValue(Object value) {
        return formatter.write((boolean) value);
    }

    public static boolean isBoolean(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isBoolean(String source, Formatter formatter) {
        return source.matches(formatter.getBooleanPattern());
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является булевым значением: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class<?>[]{boolean.class, Boolean.TYPE, Boolean.class};
    }
}
