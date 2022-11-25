package serialization.wrappers.complex;

import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.complex.collections.CollectionWrapper;

public abstract class ComplexWrapper extends Wrapper {
    public ComplexWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValue(String source) throws SerializationException {
        if (source == null)
            return null;
        return readValueComplex(null, source);
    }

    public abstract Object readValueComplex(String fieldName, String yaml) throws SerializationException;

    @Override
    public String writeValue(Object value) throws SerializationException {
        return writeValueComplex(null, value);
    }

    public abstract String writeValueComplex(String fieldName, Object value) throws SerializationException;

    public static boolean isComplex(Class<?> clazz) {
        return EnumWrapper.isEnum(clazz) || ObjectWrapper.isObject(clazz) ||
                CollectionWrapper.isCollection(clazz) || MapEntryWrapper.isMapEntry(clazz);
    }

    public static boolean isComplex(String source, Formatter formatter) {
        return EnumWrapper.isEnum(source, formatter) ||
                MapEntryWrapper.isMapEntry(source, formatter) ||
                ObjectWrapper.isObject(source, formatter) ||
                CollectionWrapper.isCollection(source, formatter);
    }
}
