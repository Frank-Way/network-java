package serialization.wrappers.complex.collections;

import serialization.formatters.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListWrapper extends CollectionWrapper {
    public ListWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    public static boolean isList(Class<?> clazz) {
        return contains(getWrappedClassesStatic(), clazz);
    }

    public static boolean isList(String source, Formatter formatter) {
        return isCollection(source, formatter);
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является списком: " + clazz.getCanonicalName();
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return getWrappedClassesStatic();
    }

    protected static Class<?>[] getWrappedClassesStatic() {
        return new Class[] {ArrayList.class, List.class};
    }

    @Override
    protected Stream<?> collectionToStream(Object value) {
        return ((ArrayList<?>) value).stream();
    }

    @Override
    protected Object collectionFromStream(Stream<?> value) {
        return value.collect(Collectors.toList());
    }

    @Override
    public Object unwrap(Object value, Class<?> clazz) {
        return value;
    }

    //    @Override
//    public String writeValueComplex(String fieldName, Object value) {
//        ArrayList<String> result = new ArrayList<>();
//        collectionToStream(value).forEach(item -> {
//            Class<?> itemClass = item.getClass();
//            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
//            String writtenItem = itemWrapper instanceof ComplexWrapper ?
//                    ((ComplexWrapper) itemWrapper).writeValueComplex("", item) :
//                    itemWrapper.writeValue(item);
//            result.add(writtenItem);
//        });
//        return formatter.write(fieldName, result);
//    }
//
//    @Override
//    public Object readValueComplex(String fieldName, String yaml) {
//        Collection<String> collection = formatter.readToCollection(fieldName, yaml);
//        return collection.stream().map(s -> {
//            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
//            return itemWrapper instanceof ComplexWrapper ?
//                    ((ComplexWrapper) itemWrapper).readValueComplex("", s) :
//                    itemWrapper.readValue(s);
//        }).toArray(Object[]::new);
//    }
}
