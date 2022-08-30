package tests.cases.serialization.yaml;

import serialization.serializers.Serializer;
import serialization.serializers.YamlSerializer;
import tests.cases.serialization.BaseForSerialization;

import java.nio.charset.StandardCharsets;

public abstract class BaseForSerializationYaml extends BaseForSerialization {
    @Override
    protected Serializer getSerializer() {
        return new YamlSerializer(doubleFormat);
    }

    @Override
    protected String serializedToString(byte[] source) {
        return new String(source, StandardCharsets.UTF_8);
    }
}
