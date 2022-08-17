package serialization;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import models.networks.Network;
import options.Constants;
import sun.nio.ch.Net;

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
     * Сериализация сети в файл
     * @param network сеть
     * @param path путь
     * @param filename имя файла
     * @param serializationType тип сериализации
     * @throws IIOException
     */
    public static void save(Network network, String path, String filename, SerializationType serializationType) throws SerializationException {
        try {
            switch (serializationType) {
                case JAVA:
                    saveToFile(convertToBytes(network), path, filename, JAVA_EXTENSION);
                    break;
                case YAML:
                    saveToFile(network.toYaml(0, Constants.DOUBLE_FORMAT).getBytes(StandardCharsets.UTF_8),
                            path, filename, YAML_EXTENSION);
                    break;
                default:
                    throw new IllegalArgumentException("Не известный тип сериализации: " + serializationType);
            }
        } catch (IOException ioe) {
            throw new SerializationException(ioe);
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
     * Десериализация сети из файла
     * @param path путь
     * @param filename имя файла
     * @param serializationType тип сериализации
     * @return десериализованная сеть
     * @throws IIOException
     * @throws ClassNotFoundException
     */
    public static Network load(String path, String filename, SerializationType serializationType) throws SerializationException {
        try {
            switch (serializationType) {
                case JAVA:
                    return (Network) convertFromBytes(loadFromFile(path, filename, JAVA_EXTENSION));
                case YAML:
                    return Network.fromYaml(new String(loadFromFile(path, filename, YAML_EXTENSION),
                            StandardCharsets.UTF_8), 0);
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
        return path + File.separator + filename + "." + extension;
    }
}
