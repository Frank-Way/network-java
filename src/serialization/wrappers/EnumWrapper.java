package serialization.wrappers;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import serialization.formatters.Formatter;

import java.util.HashMap;
import java.util.Map;

public class EnumWrapper extends ComplexWrapper {
    protected static final String VALUE_FIELD = "value";

    public EnumWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isEnum(clazz);
        if (result)
            return null;
        return "Класс не является перечислением: " + clazz.getCanonicalName();
    }

    public static boolean isEnum(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    protected Object readValueInner(String fieldName, String yaml) {
        Map<String, String> tree = formatter.readToMap(fieldName, yaml);
        try {
            Class enumClass = Class.forName(tree.get(CLASS_NAME_FIELD));
            return Enum.valueOf(enumClass, tree.get(VALUE_FIELD));
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    protected String writeValue(String fieldName, Object value) {
        Map<String, String> result = new HashMap<>();
        Enum<?> valueAsEnum = (Enum<?>) value;
        result.put(CLASS_NAME_FIELD, clazz.getCanonicalName());
        result.put(VALUE_FIELD, valueAsEnum.name());
        return formatter.write(fieldName, result);
    }
}
