package serialization.wrappers;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import serialization.YamlSerializationUtils;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import serialization.formatters.Formatter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ObjectWrapper extends ComplexWrapper {
    protected final static Class<YamlSerializable> typeAnnotationClass = YamlSerializable.class;
    protected final static Class<YamlField> fieldAnnotationClass = YamlField.class;

    public ObjectWrapper(Class<?> clazz, Formatter formatter) {
        super(clazz, formatter);
    }

    @Override
    protected Object readValueInner(String fieldName, String yaml) {
        yaml = formatter.removeComments(yaml);
        Field[] serializableFields = null;
        Map<String, String> tree = formatter.readToMap(fieldName, yaml);
        Class<?> serializedClazz;
        Object result = null;
        try {
            serializedClazz = Class.forName(tree.get(CLASS_NAME_FIELD));
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
                    ((ComplexWrapper) fieldWrapper).readValueInner(innerFieldName, tree.get(innerFieldName)) :
                    fieldWrapper.readValueInner(tree.get(innerFieldName));
            try {
                if (readFieldValue.getClass().isArray())
                    field.set(result, ArrayWrapper.unwrap(readFieldValue, fieldClass));
                else
                    field.set(result, readFieldValue);
            } catch (IllegalAccessException e) {
                throw new SerializationException(e);
            }
        }
        return result;
    }

    @Override
    protected String writeValue(String fieldName, Object value) {
        Field[] serializableFields = YamlSerializationUtils.getYamlFields(clazz);

        Map<String, String> result = new HashMap<>();
        result.put(CLASS_NAME_FIELD, clazz.getCanonicalName());

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
                    ((ComplexWrapper) fieldWrapper).writeValue(innerFieldName, fieldValue) :
                    fieldWrapper.writeValue(fieldValue);
            result.put(innerFieldName, writtenFieldValue);
        }
        return formatter.write(fieldName, result);
    }

    @Override
    protected String canBeWrapped() {
        boolean result = isObject(clazz);
        if (result)
            return null;
        return "Класс не является сериализуемым объектом: " + clazz.getCanonicalName();
    }

    public static boolean isObject(Class<?> clazz) {
        return clazz.isAnnotationPresent(typeAnnotationClass);
    }

    protected static boolean isClassAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    protected static Class<?> getParentAbstractClass(Class<?> clazz) {
        Class<?> parentClass = clazz.getSuperclass();
        if (parentClass.equals(Object.class) || isClassAbstract(parentClass))
            return parentClass;
        return getParentAbstractClass(parentClass);
    }

}
