package serialization.serializers;

import serialization.SerializationType;
import utils.ExceptionUtils;

public class SerializerFactory {
    protected final SerializationType serializationType;
    protected String doubleFormat;

    public SerializerFactory(SerializationType serializationType) {
        this.serializationType = serializationType;
    }

    public SerializerFactory(SerializationType serializationType, String doubleFormat) {
        this.serializationType = serializationType;
        this.doubleFormat = doubleFormat;
    }

    public Serializer createSerializer() {
        switch (serializationType) {
            case JAVA:
                return new JavaSerializer();
            case YAML:
                return new YamlSerializer(doubleFormat);
            default:
                throw new IllegalArgumentException("Не известный тип сериализации: " + serializationType);
        }
    }

    public static Serializer createSerializer(SerializationType serializationType) {
        SerializerFactory factory = new SerializerFactory(serializationType);
        return factory.createSerializer();
    }

    public static Serializer createSerializer(SerializationType serializationType, String doubleFormat) {
        SerializerFactory factory = new SerializerFactory(serializationType, doubleFormat);
        return factory.createSerializer();
    }
}
