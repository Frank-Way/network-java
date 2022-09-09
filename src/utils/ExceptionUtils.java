package utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ExceptionUtils {
    public static IllegalArgumentException newUnknownClassException(Class<?> clazz) {
        return new IllegalArgumentException("Не известный класс: " + clazz.getCanonicalName());
    }

    public static IllegalArgumentException newUnknownFormatException(String source) {
        return new IllegalArgumentException("Не известный формат: " + source);
    }

    public static IllegalArgumentException newUnknownEnumItemException(Class<?> enumClass, Enum<?> enumItem) {
        return new IllegalArgumentException(String.format("Не известный элемент \"%s\" перечисления \"%s\"",
                enumItem.name(), enumClass.getCanonicalName()));
    }

    public static IllegalArgumentException newUnknownAxisException(int given, int maxAcceptable) {
        return new IllegalArgumentException(String.format(
                "Не известная ось: %d. Доступные значения: %s",
                given, IntStream.range(0, maxAcceptable).boxed().collect(Collectors.toList())));
    }

    public static IllegalStateException newWrongBuilderException(String builderInfo) {
        return new IllegalStateException("Не корректное состояние билдера: " + builderInfo);
    }
}
