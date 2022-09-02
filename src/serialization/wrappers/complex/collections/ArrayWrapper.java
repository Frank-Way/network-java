package serialization.wrappers.complex.collections;

import serialization.formatters.Formatter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArrayWrapper extends CollectionWrapper {
    public ArrayWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

//    @Override
//    public Object readValueComplex(String fieldName, String yaml) {
//        Collection<String> collection = formatter.readToCollection(fieldName, yaml);
//        Class<?> itemClass = getClassOfArrayItems(clazz);
//        return collection.stream().map(s -> {
//            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
//            return itemWrapper instanceof ComplexWrapper ?
//                    ((ComplexWrapper) itemWrapper).readValueComplex("", s) :
//                    itemWrapper.readValue(s);
//        }).toArray(Object[]::new);
//    }
//
//    @Override
//    public String writeValueComplex(String fieldName, Object value) {
//        ArrayList<String> result = new ArrayList<>();
//        for (int i = 0; i < Array.getLength(value); i++) {
//            Object item = Array.get(value, i);
//            Class<?> itemClass = item.getClass();
//            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
//            String writtenItem = itemWrapper instanceof ComplexWrapper ?
//                    ((ComplexWrapper) itemWrapper).writeValueComplex("", item) :
//                    itemWrapper.writeValue(item);
//            result.add(writtenItem);
//        }
//        return formatter.write(fieldName, result);
//    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является массивом: " + clazz.getCanonicalName();
    }

    @Override
    public boolean canBeWrapped() {
        return isArray(clazz);
    }

    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    public static boolean isArray(String source, Formatter formatter) {
        return isCollection(source, formatter);
    }

    public static boolean isArray(Object value) {
        return isArray(value.getClass());
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return new Class[0];
    }

    @Override
    protected Stream<?> collectionToStream(Object value) {
        if (value.getClass().getComponentType().isPrimitive())
            return Arrays.stream(wrapPrimitiveArray(value));
        return Arrays.stream((Object[]) value);
    }

    protected Object[] wrapPrimitiveArray(Object value) {
        return IntStream.range(0, Array.getLength(value)).mapToObj(i -> Array.get(value, i)).toArray(Object[]::new);
    }

    @Override
    protected Object collectionFromStream(Stream<?> value) {
        return value.toArray(Object[]::new);
    }

    @Override
    public Object unwrap(Object value, Class<?> targetClazz) {
        if (!isArray(value))
            throw new IllegalArgumentException("Объект не является массивом: " + value);

        if (isArray(targetClazz))
            return unbox(value, targetClazz);

        // иначе - нужно привести к коллекции
        if (!contains(targetClazz.getInterfaces(), Collection.class))
            throw new IllegalArgumentException("Целевой тип не является коллекцией: " + clazz.getCanonicalName());

        return toCollection(value, targetClazz);
    }

    protected Object unbox(Object value, Class<?> targetClazz) {
        int size = Array.getLength(value);
        Object unboxed;
        Class<?> targetClassItemsClass = getClassOfArrayItems(targetClazz);
        if (isMultidimensionalArray(value)) {
            unboxed = Array.newInstance(targetClassItemsClass, size);
            for (int i = 0; i < size; i++)
                Array.set(unboxed, i, unbox(Array.get(value, i), targetClassItemsClass));
        } else {
            unboxed = Array.newInstance(targetClassItemsClass, size);
            for (int i = 0; i < size; i++)
                Array.set(unboxed, i, Array.get(value, i));
        }
        return unboxed;
    }

    protected Object toCollection(Object value, Class<?> targetClazz) {
        int size = Array.getLength(value);
        Collection<Object> result = (Collection<Object>) createCollection(targetClazz);
        for (int i = 0; i < size; i++)
            result.add(Array.get(value, i));
        return result;
    }
//
//    protected static boolean isMultidimensionalArray(Class<?> clazz) {
//        return isArray(clazz) && isArray(clazz.getComponentType());
//    }
//
    protected boolean isMultidimensionalArray(Object array) {
        return isArray(array) && isArray(getFirstElement(array));
    }
//
//    protected static boolean isArray(Object array) {
//        return isArray(array.getClass());
//    }
//
    protected Class<?> getClassOfArrayItems(Class<?> clazz) {
        return clazz.getComponentType();
    }
//
////    protected static Class<?> getBaseClass(Class<?> clazz) {
////        if (isArray(clazz))
////            return getBaseClass(getClassOfArrayItems(clazz));
////        return clazz;
////    }
//
//    protected static int calculateDimensions(Class<?> clazz) {
//        int result = 0;
//        while (isArray(clazz)) {
//            clazz = getClassOfArrayItems(clazz);
//            result++;
//        }
//        return result;
//    }
//
//    protected static int calculateDimensions(Object array) {
//        int result = 0;
//        while (array.getClass().isArray()) {
//            array = getFirstElement(array);
//            result++;
//        }
//        return result;
//    }
//
//    public Object unwrap(Object array, Class<?> targetClass) {
//        if (!isArray(array))
//            throw new IllegalArgumentException("Объект не является массивом: " + array);
//
//        int targetArrayDimensions = calculateDimensions(targetClass);
//        int givenArrayDimensions = calculateDimensions(array);
//        if (targetArrayDimensions != givenArrayDimensions)
//            throw new IllegalArgumentException(String.format(
//                    "Размерность полученного массива не соответствует типу (%d и %d, соответственно)",
//                    givenArrayDimensions, targetArrayDimensions));
//
////        Class<?> baseTargetClass = getBaseClass(targetClass);
//        return unbox(array, targetClass);
//    }
//
//    private static Object unbox(Object array, Class<?> targetClass) {
//        int size = Array.getLength(array);
//        Object unboxed;
//        Class<?> targetClassItemsClass = getClassOfArrayItems(targetClass);
//        if (isMultidimensionalArray(array)) {
//            unboxed = Array.newInstance(targetClassItemsClass, size);
//            for (int i = 0; i < size; i++)
//                Array.set(unboxed, i, unbox(Array.get(array, i), targetClassItemsClass));
//        } else {
//            unboxed = Array.newInstance(targetClassItemsClass, size);
//            for (int i = 0; i < size; i++)
//                Array.set(unboxed, i, Array.get(array, i));
//        }
//        return unboxed;
//    }
}
