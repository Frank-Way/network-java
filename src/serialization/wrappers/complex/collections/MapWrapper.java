package serialization.wrappers.complex.collections;

import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;
import serialization.wrappers.complex.ComplexWrapper;
import serialization.wrappers.complex.MapEntryWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapWrapper extends CollectionWrapper {
    protected final static String MAP_ENTRY_FIELD = COLLECTION_ITEM_FIELD + "#map.entry";

    public MapWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public static boolean isMap(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isMap(String source, Formatter formatter) {
        if (!isCollection(source, formatter))
            return false;
        try {
            Collection<String> tree = formatter.readToCollection(null, source);
            return tree.stream()
                    .map(string -> MapEntryWrapper.isMapEntry(string, formatter))
                    .filter(b -> !b).findAny().orElse(true);
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является ассоциативным массивом: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class[] {HashMap.class};
    }

    @Override
    public String writeValueComplex(String fieldName, Object value) throws SerializationException {
        ArrayList<String> result = new ArrayList<>();
        for (Object item: collectionToStream(value).collect(Collectors.toList())) {
            Class<?> itemClass = item.getClass();
            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
            result.add(itemWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) itemWrapper).writeValueComplex(MAP_ENTRY_FIELD, item) :
                    itemWrapper.writeValue(item));
        }
        return formatter.write(fieldName, result);
    }

    @Override
    protected Stream<?> collectionToStream(Object value) {
        return ((HashMap<?, ?>) value).entrySet().stream();
    }

    @Override
    protected Object collectionFromStream(Stream<?> value) {
        HashMap<Object, Object> result = new HashMap<>();
        value.forEach(item -> {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) item;
            result.put(entry.getKey(), entry.getValue());
        });
        return result;
    }

    @Override
    public Object unwrap(Object value, Class<?> clazz) {
        return value;
    }

    @Override
    public Object readValueComplex(String fieldName, String yaml) throws SerializationException {
        Collection<String> strings = formatter.readToCollection(fieldName, yaml);
        Collection<Object> objects = new ArrayList<>();
        for (String string: strings) {
            MapEntryWrapper entryWrapper = new MapEntryWrapper(
                    MapEntryWrapper.getClassFromString(string, formatter), formatter);
            objects.add(entryWrapper.readValueComplex(MAP_ENTRY_FIELD, string));
        }
        return collectionFromStream(objects.stream());
    }
}
