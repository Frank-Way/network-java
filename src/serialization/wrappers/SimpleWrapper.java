package serialization.wrappers;

import serialization.formatters.Formatter;

public abstract class SimpleWrapper extends Wrapper{
    public SimpleWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }
}
