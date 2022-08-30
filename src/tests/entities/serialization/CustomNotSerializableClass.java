package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

public class CustomNotSerializableClass extends TestSerializableClass {
    @YamlField protected int serializableField;
    protected int nonSerializableField;

    public CustomNotSerializableClass(int serializableField, int nonSerializableField) {
        this.serializableField = serializableField;
        this.nonSerializableField = nonSerializableField;
    }

    @Override
    public void set(ValuesProvider valuesProvider) {
        serializableField = valuesProvider.getInteger();
        nonSerializableField = valuesProvider.getInteger();
    }

    @Override
    public String toString() {
        return "CustomNotSerializableClass{" +
                "serializableField=" + serializableField +
                ", nonSerializableField=" + nonSerializableField +
                '}';
    }
}
