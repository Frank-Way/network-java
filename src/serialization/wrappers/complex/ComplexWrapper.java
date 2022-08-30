package serialization.wrappers.complex;

import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.complex.collections.ArrayWrapper;
import serialization.wrappers.complex.collections.CollectionWrapper;

public abstract class ComplexWrapper extends Wrapper {
    public ComplexWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValue(String source) {
        return readValueComplex(null, source);
    }

    public abstract Object readValueComplex(String fieldName, String yaml);

    @Override
    public String writeValue(Object value) {
        return writeValueComplex(null, value);
    }

    public abstract String writeValueComplex(String fieldName, Object value);

    public static boolean isComplex(Class<?> clazz) {
        return EnumWrapper.isEnum(clazz) || ObjectWrapper.isObject(clazz) ||
                CollectionWrapper.isCollection(clazz) || MapEntryWrapper.isMapEntry(clazz);
    }

    public static boolean isComplex(String source, Formatter formatter) {
        return EnumWrapper.isEnum(source, formatter) || ObjectWrapper.isObject(source, formatter) ||
                CollectionWrapper.isCollection(source, formatter) || MapEntryWrapper.isMapEntry(source, formatter);
    }
}
