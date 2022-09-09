package tests.entities.serialization;

import serialization.annotations.YamlSerializable;
import tests.utils.ValuesProvider;

import java.util.Random;

@YamlSerializable
public abstract class TestSerializableClass {
    public TestSerializableClass() {
    }

    public abstract void set(ValuesProvider valuesProvider);

    protected static int getRandomSize() {
        return 5 + new Random().nextInt(5);
    }

    @Override
    public abstract String toString();
}
