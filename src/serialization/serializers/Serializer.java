package serialization.serializers;

import serialization.exceptions.SerializationException;

public interface Serializer {
    byte[] serialize(Object data) throws SerializationException;
    Object deserialize(byte[] data, Class<?> clazz) throws SerializationException;
    String getFileExtension();

//    default String getFileExtension(SerializationType serializationType) {
//        switch (serializationType) {
//            case YAML:
//                return "yaml";
//            case JAVA:
//            default:
//                return ".dat";
//        }
//    }
}
