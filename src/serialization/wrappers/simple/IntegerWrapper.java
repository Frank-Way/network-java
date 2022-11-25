package serialization.wrappers.simple;

import serialization.formatters.Formatter;

public class IntegerWrapper extends SimpleWrapper{
    public IntegerWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValue(String yaml) {
        if (yaml == null)
            return 0;
        return formatter.readInteger(yaml);
    }

    @Override
    public String writeValue(Object value) {
        return formatter.write((int) value);
    }

    public static boolean isInteger(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isInteger(String source, Formatter formatter) {
        return source.matches(formatter.getIntegerPattern());
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является целым числом: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class<?>[]{int.class, Integer.TYPE, Integer.class};
    }
}
