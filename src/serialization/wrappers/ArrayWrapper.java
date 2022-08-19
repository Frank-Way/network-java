package serialization.wrappers;

import serialization.formatters.Formatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class ArrayWrapper extends ComplexWrapper {
    public ArrayWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected Object readValueInner(String fieldName, String yaml) {
        yaml = formatter.removeComments(yaml);
        Collection<String> collection = formatter.readToCollection(fieldName, yaml);
        Class<?> itemClass = getClassOfArrayItems(clazz);
        return collection.stream().map(s -> {
            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
            return itemWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) itemWrapper).readValueInner("", s) :
                    itemWrapper.readValueInner(s);
        }).toArray(Object[]::new);
    }

    @Override
    protected String writeValue(String fieldName, Object value) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < Array.getLength(value); i++) {
            Object item = Array.get(value, i);
            Class<?> itemClass = item.getClass();
            Wrapper itemWrapper = WrapperFactory.createWrapper(itemClass, formatter);
            String writtenItem = itemWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) itemWrapper).writeValue("", item) :
                    itemWrapper.writeValue(item);
            result.add(writtenItem);
        }
        return formatter.write(fieldName, result);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isArray(clazz);
        // Проверка возможности обернуть базовый класс. В противном случае вылетит IllegalArgumentException
        if (result)
            try {
                WrapperFactory.classToWrapperType(getBaseClass(clazz));
                return null;
            } catch (IllegalArgumentException iae) {
                return "Элементы массива не могут быть обёрнуты";
            }
        else
            return "Класс не является массивом: " + clazz.getCanonicalName();
    }

    protected static Object getFirstElement(Object array) {
        return Array.get(array, 0);
    }

    protected static boolean isMultidimensionalArray(Class<?> clazz) {
        return isArray(clazz) && isArray(clazz.getComponentType());
    }

    protected static boolean isMultidimensionalArray(Object array) {
        return isArray(array) && isArray(getFirstElement(array));
    }

    protected static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    protected static boolean isArray(Object array) {
        return array.getClass().isArray();
    }

    protected static Class<?> getClassOfArrayItems(Class<?> clazz) {
        return clazz.getComponentType();
    }

    protected static Class<?> getBaseClass(Class<?> clazz) {
        if (isArray(clazz))
            return getBaseClass(getClassOfArrayItems(clazz));
        return clazz;
    }

    protected static int calculateDimensions(Class<?> clazz) {
        int result = 0;
        while (isArray(clazz)) {
            clazz = getClassOfArrayItems(clazz);
            result++;
        }
        return result;
    }

    protected static int calculateDimensions(Object array) {
        int result = 0;
        while (array.getClass().isArray()) {
            array = getFirstElement(array);
            result++;
        }
        return result;
    }

    public static Object unwrap(Object array, Class<?> targetClass) {
        if (!isArray(array))
            throw new IllegalArgumentException("Объект не является массивом: " + array);

        int targetArrayDimensions = calculateDimensions(targetClass);
        int givenArrayDimensions = calculateDimensions(array);
        if (targetArrayDimensions != givenArrayDimensions)
            throw new IllegalArgumentException(String.format(
                    "Размерность полученного массива не соответствует типу (%d и %d, соответственно)",
                    givenArrayDimensions, targetArrayDimensions));

        Class<?> baseTargetClass = getBaseClass(targetClass);
        return unbox(array, targetClass);
    }

    private static Object unbox(Object array, Class<?> targetClass) {
        int size = Array.getLength(array);
        Object unboxed;
        Class<?> targetClassItemsClass = getClassOfArrayItems(targetClass);
        if (isMultidimensionalArray(array)) {
            unboxed = Array.newInstance(targetClassItemsClass, size);
            for (int i = 0; i < size; i++)
                Array.set(unboxed, i, unbox(Array.get(array, i), targetClassItemsClass));
        } else {
            unboxed = Array.newInstance(targetClassItemsClass, size);
            for (int i = 0; i < size; i++)
                Array.set(unboxed, i, Array.get(array, i));
        }
        return unboxed;
    }
}
