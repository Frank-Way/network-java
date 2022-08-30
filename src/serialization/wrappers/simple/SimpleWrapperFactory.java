package serialization.wrappers.simple;

import serialization.formatters.Formatter;
import serialization.wrappers.WrapperFactory;
import utils.ExceptionUtils;

public class SimpleWrapperFactory extends WrapperFactory {
    public SimpleWrapperFactory(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public SimpleWrapperFactory(String source, Formatter formatter) {
        super(source, formatter);
    }

    @Override
    public SimpleWrapper createWrapper() {
        if (BooleanWrapper.isBoolean(clazz))
            return new BooleanWrapper(clazz, formatter);
        if (IntegerWrapper.isInteger(clazz))
            return new IntegerWrapper(clazz, formatter);
        if (DoubleWrapper.isDouble(clazz))
            return new DoubleWrapper(clazz, formatter);
        if (StringWrapper.isString(clazz))
            return new StringWrapper(clazz, formatter);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

    @Override
    public SimpleWrapper createWrapperByString() {
        if (BooleanWrapper.isBoolean(source, formatter))
            return new BooleanWrapper(Boolean.class, formatter);
        if (IntegerWrapper.isInteger(source, formatter))
            return new IntegerWrapper(Integer.class, formatter);
        if (DoubleWrapper.isDouble(source, formatter))
            return new DoubleWrapper(Double.class, formatter);
        if (StringWrapper.isString(source, formatter))
            return new StringWrapper(String.class, formatter);
        throw ExceptionUtils.newUnknownFormatException(source);
    }
}
