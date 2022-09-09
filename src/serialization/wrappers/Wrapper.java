package serialization.wrappers;

import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;

import java.util.Arrays;

public abstract class Wrapper {
    protected final Class<?> clazz;
    protected final Formatter formatter;

    public Wrapper(Class<?> clazz, Formatter formatter) {
        this.clazz = clazz;
        this.formatter = formatter;
        if (!canBeWrapped())
            throw new IllegalArgumentException(getMsgIfCanNotBeWrapped());
    }

    public abstract Object readValue(String source) throws SerializationException;

    public abstract String writeValue(Object value) throws SerializationException;

    protected abstract String getMsgIfCanNotBeWrapped();

    protected abstract Class<?>[] getWrappedClasses();

    public boolean canBeWrapped() {
        return contains(getWrappedClasses(), clazz);
    }

    protected static boolean contains(Class<?>[] classes, Class<?> clazz) {
        return Arrays.stream(classes).map(aClazz -> aClazz.equals(clazz)).filter(b -> b).findAny().orElse(false);
    }

//    protected static Class<?>[] combine(Class<?>[] ... classes) {
//        return Arrays.stream(classes).flatMap(Arrays::stream).toArray(Class[]::new);
//    }
//
//    protected static Map<Class<?>, Class<?>> getPrimitiveToWrapperMap() {
//        HashMap<Class<?>, Class<?>> result = new HashMap<>();
//        result.put(int.class, Integer.class);
//        result.put(double.class, Double.class);
//        result.put(boolean.class, Boolean.class);
//        return result;
//    }
//
//    protected static Map<Class<?>, Class<?>> getWrapperToPrimitiveMap() {
//        HashMap<Class<?>, Class<?>> result = new HashMap<>();
//        result.put(Integer.class, int.class);
//        result.put(Double.class, double.class);
//        result.put(Boolean.class, boolean.class);
//        return result;
//    }
}
