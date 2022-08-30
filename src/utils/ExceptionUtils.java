package utils;

public abstract class ExceptionUtils {
    public static IllegalArgumentException newUnknownClassException(Class<?> clazz) {
        return new IllegalArgumentException("Не известный класс: " + clazz.getCanonicalName());
    }

    public static IllegalArgumentException newUnknownFormatException(String source) {
        return new IllegalArgumentException("Не известный формат: " + source);
    }
}
