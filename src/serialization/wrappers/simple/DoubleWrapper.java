package serialization.wrappers.simple;

import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;

public class DoubleWrapper extends SimpleWrapper{
    public DoubleWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValue(String yaml) {
        return formatter.readDouble(yaml);
    }

    @Override
    public String writeValue(Object value) {
        return formatter.write((double) value);
    }

    public static boolean isDouble(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isDouble(String source, Formatter formatter) {
        return source.matches(formatter.getDoublePattern());
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является вещественным числом: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class<?>[]{double.class, Double.TYPE, Double.class};
    }
}
