package serialization.wrappers;

import serialization.formatters.Formatter;
import serialization.wrappers.complex.*;
import serialization.wrappers.simple.*;
import utils.ExceptionUtils;

public class WrapperFactory {
    protected Class<?> clazz;
    protected final Formatter formatter;
    protected String source;

    public WrapperFactory(Class<?> clazz, Formatter formatter) {
        this.clazz = clazz;
        this.formatter = formatter;
    }

    public WrapperFactory(String source, Formatter formatter) {
        this.formatter = formatter;
        this.source = source;
    }

    public Wrapper createWrapper() {
        if (SimpleWrapper.isSimple(clazz))
            return new SimpleWrapperFactory(clazz, formatter).createWrapper();
        else if (ComplexWrapper.isComplex(clazz))
            return new ComplexWrapperFactory(clazz, formatter).createWrapper();
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

    public Wrapper createWrapperByString() {
        if (SimpleWrapper.isSimple(source, formatter))
            return new SimpleWrapperFactory(source, formatter).createWrapperByString();
        else if (ComplexWrapper.isComplex(source, formatter))
            return new ComplexWrapperFactory(source, formatter).createWrapperByString();
        throw ExceptionUtils.newUnknownFormatException(source);
    }

    public static Wrapper createWrapper(Class<?> clazz, Formatter formatter) {
        WrapperFactory factory = new WrapperFactory(clazz, formatter);
        return factory.createWrapper();
    }

    public static Wrapper createWrapperByString(String source, Formatter formatter) {
        WrapperFactory factory = new WrapperFactory(source, formatter);
        return factory.createWrapperByString();
    }
}
