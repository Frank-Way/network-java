package serialization.wrappers.complex.collections;

import serialization.formatters.Formatter;
import serialization.wrappers.complex.ComplexWrapperFactory;
import utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CollectionWrapperFactory extends ComplexWrapperFactory {
    public CollectionWrapperFactory(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public CollectionWrapperFactory(String source, Formatter formatter) {
        super(source, formatter);
    }

    @Override
    public CollectionWrapper createWrapper() {
        if (MapWrapper.isMap(clazz))
            return new MapWrapper(clazz, formatter);
        else if (ArrayWrapper.isArray(clazz))
            return new ArrayWrapper(clazz, formatter);
        else if (ListWrapper.isList(clazz))
            return new ListWrapper(clazz, formatter);
        else if (SetWrapper.isSet(clazz))
            return new SetWrapper(clazz, formatter);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

    @Override
    public CollectionWrapper createWrapperByString() {
        if (MapWrapper.isMap(source, formatter))
            return new MapWrapper(HashMap.class, formatter);
        if (ArrayWrapper.isArray(source, formatter))
            return new ArrayWrapper(Object[].class, formatter);
        else if (ListWrapper.isList(source, formatter))
            return new ListWrapper(ArrayList.class, formatter);
        else if (SetWrapper.isSet(source, formatter))
            return new SetWrapper(HashSet.class, formatter);
        throw ExceptionUtils.newUnknownFormatException(source);
    }
}
