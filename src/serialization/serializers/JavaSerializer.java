package serialization.serializers;

import serialization.exceptions.SerializationException;

import java.io.*;

public class JavaSerializer implements Serializer {

    @Override
    public byte[] serialize(Object data) throws SerializationException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(data);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Ошибка во время сериализации: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] data, Class<?> clazz) throws SerializationException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Ошибка во время десериализации: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileExtension() {
        return "dat";
    }

    @Override
    public String toString() {
        return "JavaSerializer{}";
    }
}
