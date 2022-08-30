package serialization.wrappers.simple;

import serialization.formatters.Formatter;

public class StringWrapper extends SimpleWrapper{
    public StringWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValue(String yaml) {
        return formatter.readString(yaml);
    }

    @Override
    public String writeValue(Object value) {
        return formatter.write(value.toString());
    }

    public static boolean isString(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isString(String source, Formatter formatter) {
        return source.matches(formatter.getStringPattern());
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является строкой: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class<?>[]{String.class};
    }
}
