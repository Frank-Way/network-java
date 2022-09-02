package serialization.wrappers.complex;

import serialization.exceptions.SerializationException;
import serialization.YamlSerializationUtils;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import serialization.formatters.Formatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;
import serialization.wrappers.complex.collections.ArrayWrapper;
import serialization.wrappers.complex.collections.CollectionWrapper;
import serialization.wrappers.complex.collections.CollectionWrapperFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ObjectWrapper extends ComplexWrapper {
    protected final static Class<YamlSerializable> typeAnnotationClass = YamlSerializable.class;
    protected final static Class<YamlField> fieldAnnotationClass = YamlField.class;

    public final static String OBJECT_CLASS_FIELD = "object.class";

    public ObjectWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    public Object readValueComplex(String fieldName, String yaml) throws SerializationException {
        Field[] serializableFields = null;
        Map<String, String> tree = formatter.readToMap(fieldName, yaml);
        Class<?> serializedClazz;
        Object result = null;
        try {
            serializedClazz = Class.forName(tree.get(OBJECT_CLASS_FIELD));
            serializableFields = YamlSerializationUtils.getYamlFields(serializedClazz);
            Constructor<?> constructor = serializedClazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            result = constructor.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SerializationException(e);
        }
        for (Field field: serializableFields) {
            YamlField yamlField = field.getAnnotation(fieldAnnotationClass);
            String innerFieldName = yamlField.fieldName();
            if (innerFieldName.isEmpty())
                innerFieldName = field.getName();
            field.setAccessible(true);

            Class<?> fieldClass = field.getType();

            Wrapper fieldWrapper = WrapperFactory.createWrapper(fieldClass, formatter);
            Object readFieldValue = fieldWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) fieldWrapper).readValueComplex(innerFieldName, tree.get(innerFieldName)) :
                    fieldWrapper.readValue(tree.get(innerFieldName));
            try {
                if (CollectionWrapper.isCollection(readFieldValue.getClass())) {
                    CollectionWrapper collectionWrapper = (CollectionWrapper) CollectionWrapperFactory.createWrapper(readFieldValue.getClass(), formatter);
                    Object unwrapped = collectionWrapper.unwrap(readFieldValue, fieldClass);
                    field.set(result, unwrapped);
                }
                else
                    field.set(result, readFieldValue);
            } catch (IllegalAccessException e) {
                throw new SerializationException(e);
            }
        }
        return result;
    }

    @Override
    public String writeValueComplex(String fieldName, Object value) throws SerializationException {
        Field[] serializableFields = YamlSerializationUtils.getYamlFields(clazz);

        Map<String, String> result = new HashMap<>();
        result.put(OBJECT_CLASS_FIELD, clazz.getCanonicalName());

        for (Field field: serializableFields) {
            YamlField yamlField = field.getAnnotation(fieldAnnotationClass);
            String innerFieldName = yamlField.fieldName();
            if (innerFieldName.isEmpty())
                innerFieldName = field.getName();
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(value);
            } catch (IllegalAccessException e) {
                throw new SerializationException(e);
            }
            Class<?> fieldClass = fieldValue.getClass();

            Wrapper fieldWrapper = WrapperFactory.createWrapper(fieldClass, formatter);
            String writtenFieldValue = fieldWrapper instanceof ComplexWrapper ?
                    ((ComplexWrapper) fieldWrapper).writeValueComplex(innerFieldName, fieldValue) :
                    fieldWrapper.writeValue(fieldValue);
            result.put(innerFieldName, writtenFieldValue);
        }
        return formatter.write(fieldName, result, OBJECT_CLASS_FIELD);
    }

    @Override
    protected String getMsgIfCanNotBeWrapped() {
        return "Класс не является сериализуемым объектом: " + clazz.getCanonicalName();
    }

    @Override
    public boolean canBeWrapped() {
        return isObject(clazz);
    }

    @Override
    protected Class<?>[] getWrappedClasses() {
        return new Class[0];
    }

    public static boolean isObject(Class<?> clazz) {
        return clazz.isAnnotationPresent(typeAnnotationClass);
    }

    public static boolean isObject(String source, Formatter formatter) {
        String tmp = formatter.getObjectPattern();
        return source.matches(formatter.getObjectPattern());
    }

    protected static boolean isClassAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    protected static Class<?> getClassFromString(String source, Formatter formatter) {
        if (!isObject(source, formatter))
            throw new IllegalArgumentException();
        Map<String, String> tree = formatter.readToMap(null, source);
        try {
            return Class.forName(tree.get(OBJECT_CLASS_FIELD));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

//    protected static Class<?> getParentAbstractClass(Class<?> clazz) {
//        Class<?> parentClass = clazz.getSuperclass();
//        if (parentClass.equals(Object.class) || isClassAbstract(parentClass))
//            return parentClass;
//        return getParentAbstractClass(parentClass);
//    }
}
