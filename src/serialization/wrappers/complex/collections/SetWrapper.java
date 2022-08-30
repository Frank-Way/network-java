package serialization.wrappers.complex.collections;

import serialization.formatters.Formatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetWrapper extends CollectionWrapper {
    public SetWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public static boolean isSet(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isSet(String source, Formatter formatter) {
        return source.matches(formatter.getCollectionPattern());
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является множеством: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class[] {HashSet.class, Set.class};
    }

    @Override
    protected Stream<?> collectionToStream(Object value) {
        return ((HashSet<?>) value).stream();
    }

    @Override
    protected Object collectionFromStream(Stream<?> value) {
        return value.collect(Collectors.toSet());
    }

    @Override
    public Object unwrap(Object value, Class<?> targetClazz) {
        return value;
    }
}
