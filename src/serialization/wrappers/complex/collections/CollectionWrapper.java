package serialization.wrappers.complex.collections;

import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;
import serialization.wrappers.complex.ComplexWrapper;
import utils.ExceptionUtils;

import java.util.*;
import java.util.stream.Stream;

public abstract class CollectionWrapper extends ComplexWrapper {
    public final static String COLLECTION_ITEM_FIELD = "collection.item";

    public CollectionWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public static boolean isCollection(Class<?> clazz) {
        return ArrayWrapper.isArray(clazz) || ListWrapper.isList(clazz) ||
                SetWrapper.isSet(clazz) || MapWrapper.isMap(clazz);
    }

    public static boolean isCollection(String source, Formatter formatter) {
        return source.matches(formatter.getCollectionPattern());
    }

    @Override
    public Object readValueComplex(String fieldName, String yaml) {
        Collection<String> strings = formatter.readToCollection(fieldName, yaml);
        return collectionFromStream(strings.stream().map(string -> {
            Wrapper itemWrapper = WrapperFactory.createWrapperByString(string, formatter);
            return itemWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) itemWrapper).readValueComplex("", string) :
                    itemWrapper.readValue(string);
        }));
    }

    @Override
    public String writeValueComplex(String fieldName, Object value) {
        ArrayList<String> result = new ArrayList<>();
        collectionToStream(value).forEach(item -> {
            Class<?> itemClass = item.getClass();
            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
            String writtenItem = itemWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) itemWrapper).writeValueComplex(COLLECTION_ITEM_FIELD, item) :
                    itemWrapper.writeValue(item);
            result.add(writtenItem);
        });
        return formatter.write(fieldName, result);
    }

    protected int getSize(Object value) {
        return (int) collectionToStream(value).count();
    }

    protected Object getFirstElement(Object value) {
        return collectionToStream(value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Коллекция пуста"));
    }

    protected boolean isNested(Object value) {
        Object firstElement = getFirstElement(value);
        return isCollection(firstElement.getClass());
    }

    protected int calculateNestingLevel(Object value) {
        Object firstElement = getFirstElement(value);
        int nestingLevel = 1;
        while (isCollection(firstElement.getClass())) {
            firstElement = getFirstElement(firstElement);
            nestingLevel++;
        }
        return nestingLevel;
    }

    protected abstract Stream<?> collectionToStream(Object value);

    protected abstract Object collectionFromStream(Stream<?> value);

    public abstract Object unwrap(Object value, Class<?> clazz);

    protected static Object createCollection(Class<?> clazz) {
        if (contains(ListWrapper.getWrappedClassesStatic(), clazz))
            return new ArrayList<>();
        else if (contains(SetWrapper.getWrappedClassesStatic(), clazz))
            return new HashSet<>();
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

//    protected Iterator<?> getIterator(Object value) {
//        return ((Collection<?>) value).iterator();
//    }
}
