package tests.cases.serialization;

import serialization.YamlSerializationUtils;
import serialization.exceptions.SerializationException;
import serialization.serializers.Serializer;
import serialization.serializers.YamlSerializer;
import tests.TestStatus;
import tests.cases.CaseWrapper;
import tests.entities.serialization.TestSerializableClass;
import tests.utils.ValuesProvider;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseForSerialization extends CaseWrapper {
    
    protected final static String doubleFormat = "%13.8f";

    protected TestStatus innerTest(Object object, Boolean isDeserializationRequired, Serializer serializer) {
        System.out.println(new Date() + "    " + "object: " + object);
        byte[] objectSerialized = null;
        try {
            objectSerialized = serializer.serialize(object);
        } catch (SerializationException se) {
            System.out.println(new Date() + "    " + se.getMessage());
            return TestStatus.FAILED;
        }
        if (serializer instanceof YamlSerializer)
            System.out.println(new Date() + "    " + "objectSerialized:\n" + 
                    new String(objectSerialized, StandardCharsets.UTF_8));
        if (isDeserializationRequired) {
            Object objectDeserialized = null;
            try {
                objectDeserialized = serializer.deserialize(
                        objectSerialized, object.getClass());
            } catch (SerializationException se) {
                System.out.println(new Date() + "    " + se.getMessage());
                return TestStatus.FAILED;
            }
            System.out.println(new Date() + "    " + "objectDeserialized: " + objectDeserialized);

            boolean isEquals = YamlSerializationUtils.isEqualsYamlSerializable(object,
                    objectDeserialized);
            System.out.println(new Date() + "    " + "isEquals: " + isEquals);
            if (!isEquals) {
                System.out.println(new Date() + "    " + "Объекты не совпали\nobject: " +
                        object + "\nobjectDeserialized: " +
                        objectDeserialized);
                return TestStatus.FAILED;
            }
            else {
                System.out.println(new Date() + "    " + "Объекты совпали");
                return TestStatus.PASSED;
            }
        }
        return TestStatus.PASSED;
    }
    
    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }

    protected static Class<?>[] convertObjectsToClasses(Object[] objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .toArray(Class[]::new);
    }

    protected static String[] convertObjectsToClassNames(Object[] objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .map(Class::getCanonicalName)
                .toArray(String[]::new);
    }

    protected static String convertObjectsToJoinedClassNames(Object[] objects) {
        return Arrays.stream(objects)
                .map(Object::getClass)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", "));
    }
}
