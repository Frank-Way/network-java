package serialization;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import serialization.formatters.Formatter;
import serialization.formatters.yaml.YamlFormatter;
import serialization.wrappers.ObjectWrapper;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;

import javax.imageio.IIOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
        try {
            switch (serializationType) {
                case JAVA:
                    saveToFile(convertToBytes(object), path, filename, JAVA_EXTENSION);
                    break;
                case YAML:
                    Formatter formatter = new YamlFormatter(doubleFormat);
                    Wrapper wrapper = WrapperFactory.createWrapper(object.getClass(), formatter);
                    saveToFile(wrapper.writeValue(object).getBytes(StandardCharsets.UTF_8),
                            path, filename, YAML_EXTENSION);
                    break;
                default:
                    throw new IllegalArgumentException("Не известный тип сериализации: " + serializationType);
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private static void saveToFile(byte[] data, String path, String filename, String extension) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            logger.severe("Ошибка при создании папки для сохранения нейросетей: " + e.getMessage());
            throw new SerializationException(e.getMessage(), e);
        }

        String fullPath = getFullPath(path, filename, extension);

        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(data);
        } catch (IOException e) {
            logger.severe("Ошибка при сохранении: " + e.getMessage());
            throw new SerializationException(e.getMessage(), e);
        }
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
    public static Object load(Class<?> clazz, String path, String filename, SerializationType serializationType) throws SerializationException {
        try {
            switch (serializationType) {
                case JAVA:
                    return convertFromBytes(loadFromFile(path, filename, JAVA_EXTENSION));
                case YAML:
                    Formatter formatter = new YamlFormatter(null);
                    Wrapper wrapper = WrapperFactory.createWrapper(clazz, formatter);
                    return wrapper.readValue(new String(loadFromFile(path, filename, YAML_EXTENSION),
                            StandardCharsets.UTF_8));
                default:
                    throw new IllegalArgumentException("Не известный тип сериализации: " + serializationType);
            }
        } catch (IOException | ClassNotFoundException exception) {
            throw new SerializationException(exception);
        }
    }

    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    private static byte[] loadFromFile(String path, String filename, String extension) throws IIOException {
        String fullPath = getFullPath(path, filename, extension);
        File file = new File(fullPath);
        try (FileInputStream fis = new FileInputStream(fullPath)) {
            byte[] arr = new byte[(int)file.length()];
            fis.read(arr);
            return arr;
        } catch (IOException ioe) {
            logger.severe("Ошибка при загрузке: " + ioe.getMessage());
            throw new SerializationException(ioe.getMessage(), ioe);
        }
    }

    private static String getFullPath(String path, String filename, String extension) {
        if (!path.isEmpty())
            return path + File.separator + filename + "." + extension;
        else return filename + "." + extension;
    }
}
