package serialization.serializers;

import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;
import serialization.formatters.yaml.YamlFormatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;

import java.nio.charset.StandardCharsets;

public class YamlSerializer implements Serializer{
    protected final String doubleFormat;

    public YamlSerializer(String doubleFormat) {
        this.doubleFormat = doubleFormat;
    }

    @Override
    public byte[] serialize(Object data) throws SerializationException {
        try {
            Formatter formatter = new YamlFormatter(doubleFormat);
            Wrapper wrapper = WrapperFactory.createWrapper(data.getClass(), formatter);
            return wrapper.writeValue(data).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializationException("Ошибка во время сериализации: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] data, Class<?> clazz) throws SerializationException {
        try {
            Formatter formatter = new YamlFormatter(doubleFormat);
            Wrapper wrapper = WrapperFactory.createWrapper(clazz, formatter);
            return wrapper.readValue(new String(data, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new SerializationException("Ошибка во время десериализации: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "YamlSerializer{" +
                "doubleFormat='" + doubleFormat + '\'' +
                '}';
    }

    @Override
    public String getFileExtension() {
        return "yaml";
    }
}
