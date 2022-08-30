package serialization;

import serialization.serializers.Serializer;
import serialization.serializers.SerializerFactory;
import serialization.exceptions.SerializationException;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class SerializationUtils {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String JAVA_EXTENSION = "dat";
    private static final String YAML_EXTENSION = "yaml";
    /**
     * Сериализация объекта в файл
     * @param object объект
     * @param path путь
     * @param filename имя файла
     * @param serializationType тип сериализации
     * @param doubleFormat формат вещественных чисел
     */
    public static void save(Object object, String path, String filename,
                            SerializationType serializationType, String doubleFormat) throws SerializationException {
        Serializer serializer = SerializerFactory.createSerializer(serializationType, doubleFormat);
        byte[] serializedObject = serializer.serialize(object);

        try {
            saveToFile(serializedObject, path, filename, serializer.getFileExtension());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private static void saveToFile(byte[] data, String path, String filename, String extension) throws IOException{
        Files.createDirectories(Paths.get(path));
        String fullPath = getFullPath(path, filename, extension);

        FileOutputStream fos = new FileOutputStream(fullPath);
        fos.write(data);

        logger.fine("Сохранена нейросеть в файл: " + fullPath);
    }

    /**
     * Десериализация объекта из файла
     * @param clazz класс объекта
     * @param path путь
     * @param filename имя файла
     * @param serializationType тип сериализации
     * @return десериализованная сеть
     */
    public static Object load(Class<?> clazz, String path, String filename, SerializationType serializationType) throws serialization.exceptions.SerializationException {
        Serializer serializer = SerializerFactory.createSerializer(serializationType);
        byte[] serializedObject;
        try {
            serializedObject = loadFromFile(path, filename, serializer.getFileExtension());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
        Object deserializedObject = serializer.deserialize(serializedObject, clazz);
        return deserializedObject;
    }

    private static byte[] loadFromFile(String path, String filename, String extension) throws IOException {
        String fullPath = getFullPath(path, filename, extension);
        File file = new File(fullPath);
        FileInputStream fis = new FileInputStream(fullPath);
        byte[] arr = new byte[(int)file.length()];
        fis.read(arr);
        return arr;
    }

    private static String getFullPath(String path, String filename, String extension) {
        if (!path.isEmpty())
            return path + File.separator + filename + "." + extension;
        else return filename + "." + extension;
    }

    public static Field[] getAllFields(Class<?> clazz) {
        Field[] clazzFields = clazz.getDeclaredFields();
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz.equals(Object.class))
            return clazzFields;
        Field[] superClassFields = getAllFields(superClazz);
        return YamlSerializationUtils.combine(clazzFields, superClassFields);
    }
}
