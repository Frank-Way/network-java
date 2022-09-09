package serialization.wrappers.complex;

import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;

import java.util.HashMap;
import java.util.Map;

public class EnumWrapper extends ComplexWrapper {
    public static final String ENUM_CLASS_FIELD = "enum.class";
    public static final String ENUM_VALUE_FIELD = "enum.value";

    public EnumWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является перечислением: " + clazz.getCanonicalName();
    }

    @Override
    public boolean canBeWrapped() {
        return isEnum(clazz);
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return new Class[0];
    }

    public static boolean isEnum(Class<?> clazz) {
        return clazz.isEnum();
    }

    public static boolean isEnum(String source, Formatter formatter) {
        try {
            Map<String, String> tree = formatter.readToMap(null, source);
            return tree.size() == 2 && tree.containsKey(ENUM_CLASS_FIELD) && tree.containsKey(ENUM_VALUE_FIELD)  &&
                    isEnum(Class.forName(tree.get(ENUM_CLASS_FIELD)));
        } catch (Exception e) {
            return false;
        }
//        return source.matches(formatter.getEnumPattern());
    }

    @Override
    public Object readValueComplex(String fieldName, String yaml) throws SerializationException {
        Map<String, String> tree = formatter.readToMap(fieldName, yaml);
        try {
            Class enumClass = Class.forName(tree.get(ENUM_CLASS_FIELD));
            return Enum.valueOf(enumClass, tree.get(ENUM_VALUE_FIELD));
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public String writeValueComplex(String fieldName, Object value) {
        Map<String, String> result = new HashMap<>();
        Enum<?> valueAsEnum = (Enum<?>) value;
        result.put(ENUM_CLASS_FIELD, clazz.getCanonicalName());
        result.put(ENUM_VALUE_FIELD, valueAsEnum.name());
        return formatter.write(fieldName, result, ENUM_CLASS_FIELD);
    }

    protected static Class<?> getClassFromString(String source, Formatter formatter) {
        if (!isEnum(source, formatter))
            throw new IllegalArgumentException();
        Map<String, String> tree = formatter.readToMap(null, source);
        try {
            return Class.forName(tree.get(ENUM_CLASS_FIELD));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }
}
