package serialization.wrappers;

import serialization.formatters.Formatter;

public class WrapperFactory {
    protected final Class<?> clazz;
    protected final Formatter formatter;

    public WrapperFactory(Class<?> clazz, Formatter formatter) {
        this.clazz = clazz;
        this.formatter = formatter;
    }

    protected static WrapperType classToWrapperType(Class<?> clazz) {
        if (BooleanWrapper.isBoolean(clazz))
            return WrapperType.BOOLEAN;
        if (IntegerWrapper.isInteger(clazz))
            return WrapperType.INTEGER;
        if (DoubleWrapper.isDouble(clazz))
            return WrapperType.DOUBLE;
        if (StringWrapper.isString(clazz))
            return WrapperType.STRING;
        if (ArrayWrapper.isArray(clazz))
            return WrapperType.ARRAY;
        if (ObjectWrapper.isObject(clazz))
            return WrapperType.OBJECT;
        if (EnumWrapper.isEnum(clazz))
            return WrapperType.ENUM;
        throw new IllegalArgumentException("Не известный класс: " + clazz.getCanonicalName());
    }

    public Wrapper createWrapper() {
        WrapperType wrapperType = classToWrapperType(clazz);
        switch (wrapperType) {
            case OBJECT:
                return new ObjectWrapper(clazz, formatter);
            case STRING:
                return new StringWrapper(clazz, formatter);
            case ARRAY:
                return new ArrayWrapper(clazz, formatter);
            case BOOLEAN:
                return new BooleanWrapper(clazz, formatter);
            case DOUBLE:
                return new DoubleWrapper(clazz, formatter);
            case ENUM:
                return new EnumWrapper(clazz, formatter);
            case INTEGER:
                return new IntegerWrapper(clazz, formatter);
            default:
                throw new IllegalArgumentException("Не известный тип: " + wrapperType);
        }
    }

    public static Wrapper createWrapper(Class<?> clazz, Formatter formatter) {
        WrapperFactory factory = new WrapperFactory(clazz, formatter);
        return factory.createWrapper();
    }
}
