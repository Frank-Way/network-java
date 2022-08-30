package serialization.wrappers.simple;

import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;

public abstract class SimpleWrapper extends Wrapper {
    public SimpleWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public static boolean isSimple(Class<?> clazz) {
        return BooleanWrapper.isBoolean(clazz) || DoubleWrapper.isDouble(clazz) ||
                IntegerWrapper.isInteger(clazz) || StringWrapper.isString(clazz);
    }

    public static boolean isSimple(String source, Formatter formatter) {
        return BooleanWrapper.isBoolean(source, formatter) || DoubleWrapper.isDouble(source, formatter) ||
                IntegerWrapper.isInteger(source, formatter) || StringWrapper.isString(source, formatter);
    }
}
