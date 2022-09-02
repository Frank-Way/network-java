package serialization.wrappers.complex;

import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;
import serialization.wrappers.complex.ComplexWrapper;

import java.util.HashMap;
import java.util.Map;

public class MapEntryWrapper extends ComplexWrapper {
    public final static String MAP_ENTRY_KEY_FIELD = "map.key";
    public final static String MAP_ENTRY_VALUE_FIELD = "map.value";

    public MapEntryWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public String writeValueComplex(String fieldName, Object rawEntry) throws SerializationException {
        Map<String, String> result = new HashMap<>();
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) rawEntry;
        Object key = entry.getKey();
        Class<?> keyClass = key.getClass();
        Wrapper keyWrapper = WrapperFactory.createWrapper(keyClass, formatter);
        String writtenKey = keyWrapper instanceof ComplexWrapper ?
                ((ComplexWrapper) keyWrapper).writeValueComplex(MAP_ENTRY_KEY_FIELD, key) :
                keyWrapper.writeValue(key);
        result.put(MAP_ENTRY_KEY_FIELD, writtenKey);
        Object value = entry.getValue();
        Class<?> valueClass = value.getClass();
        Wrapper valueWrapper = WrapperFactory.createWrapper(valueClass, formatter);
        String writtenValue = valueWrapper instanceof ComplexWrapper ?
                ((ComplexWrapper) valueWrapper).writeValueComplex(MAP_ENTRY_VALUE_FIELD, value) :
                valueWrapper.writeValue(value);
        result.put(MAP_ENTRY_VALUE_FIELD, writtenValue);
        return formatter.write(fieldName, result, MAP_ENTRY_KEY_FIELD);
    }

    @Override
    public Object readValueComplex(String fieldName, String yaml) throws SerializationException {
        HashMap<Object, Object> result = new HashMap<>();
        Map<String, String> tree = formatter.readToMap(fieldName, yaml);

        String keyString = tree.get(MAP_ENTRY_KEY_FIELD);
        Wrapper keyWrapper = WrapperFactory.createWrapperByString(keyString, formatter);
        Object key = keyWrapper instanceof ComplexWrapper ?
                ((ComplexWrapper) keyWrapper).readValueComplex(MAP_ENTRY_KEY_FIELD, keyString) :
                keyWrapper.readValue(keyString);
        String valueString = tree.get(MAP_ENTRY_VALUE_FIELD);
        Wrapper valueWrapper = WrapperFactory.createWrapperByString(valueString, formatter);
        Object value = valueWrapper instanceof ComplexWrapper ?
                ((ComplexWrapper) valueWrapper).readValueComplex(MAP_ENTRY_VALUE_FIELD, valueString) :
                valueWrapper.readValue(valueString);
        result.put(key, value);
        return result.entrySet().stream().findFirst().orElse(null);
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является элементом ассоциативного массива: " + clazz.getCanonicalName();
    }

    @Override
    public boolean canBeWrapped() {
        return contains(clazz.getInterfaces(), Map.Entry.class);
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return null;
    }

    public static boolean isMapEntry(Class<?> clazz) {
        return contains(clazz.getInterfaces(), Map.Entry.class);
    }

    public static boolean isMapEntry(String source, Formatter formatter) {
        String tmp = formatter.getMapEntryPattern();
        return source.matches(formatter.getMapEntryPattern());
    }

    public static Class<?> getClassFromString(String source, Formatter formatter) {
        if (!isMapEntry(source, formatter))
            throw new IllegalArgumentException();
        Map<String, String> tree = formatter.readToMap(null, source);
        return tree.entrySet().stream().findAny().orElseThrow(IllegalArgumentException::new).getClass();
    }
}
