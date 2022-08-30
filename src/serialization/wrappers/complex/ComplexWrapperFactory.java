package serialization.wrappers.complex;

import serialization.formatters.Formatter;
import serialization.wrappers.WrapperFactory;
import serialization.wrappers.complex.collections.CollectionWrapper;
import serialization.wrappers.complex.collections.CollectionWrapperFactory;
import utils.ExceptionUtils;

import java.util.HashMap;

public class ComplexWrapperFactory extends WrapperFactory {
    public ComplexWrapperFactory(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public ComplexWrapperFactory(String source, Formatter formatter) {
        super(source, formatter);
    }

    @Override
    public ComplexWrapper createWrapper() {
        if (EnumWrapper.isEnum(clazz))
            return new EnumWrapper(clazz, formatter);
        else if (ObjectWrapper.isObject(clazz))
            return new ObjectWrapper(clazz, formatter);
        else if (MapEntryWrapper.isMapEntry(clazz))
            return new MapEntryWrapper(clazz, formatter);
        else if (CollectionWrapper.isCollection(clazz))
            return new CollectionWrapperFactory(clazz, formatter).createWrapper();
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

    @Override
    public ComplexWrapper createWrapperByString() {
        if (EnumWrapper.isEnum(source, formatter))
            return new EnumWrapper(EnumWrapper.getClassFromString(source, formatter), formatter);
        else if (ObjectWrapper.isObject(source, formatter))
            return new ObjectWrapper(ObjectWrapper.getClassFromString(source, formatter), formatter);
        else if (MapEntryWrapper.isMapEntry(source, formatter))
            return new MapEntryWrapper(MapEntryWrapper.getClassFromString(source, formatter), formatter);
        else if (CollectionWrapper.isCollection(source, formatter))
            return new CollectionWrapperFactory(source, formatter).createWrapperByString();
        throw ExceptionUtils.newUnknownFormatException(source);
    }
}
