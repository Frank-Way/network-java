package tests.entities.serialization;

import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import tests.utils.ValuesProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@YamlSerializable
public class CustomSerializableClass extends TestSerializableClass {
    @YamlField protected int serializableField;
    protected int nonSerializableField;

    public CustomSerializableClass() {
    }

    public CustomSerializableClass(int serializableField, int nonSerializableField) {
        this.serializableField = serializableField;
        this.nonSerializableField = nonSerializableField;
    }

    @Override
    public void set(ValuesProvider valuesProvider) {
        serializableField = valuesProvider.getInteger();
        nonSerializableField = valuesProvider.getInteger();
    }
    
    public static CustomSerializableClass[] getArray(ValuesProvider valuesProvider, int size) {
        return IntStream.range(0, size).parallel().mapToObj(i -> {
            CustomSerializableClass newInstance = new CustomSerializableClass();
            newInstance.set(valuesProvider);
            return newInstance;
        }).toArray(CustomSerializableClass[]::new);
    }
    
    public static CustomSerializableClass[][] getNestedArray(ValuesProvider valuesProvider, int size1, int size2) {
        return IntStream.range(0, size1).parallel().mapToObj(i -> 
            IntStream.range(0, size2).parallel().mapToObj(j -> {
                CustomSerializableClass newInstance = new CustomSerializableClass();
                newInstance.set(valuesProvider);
                return newInstance;
            }).toArray(CustomSerializableClass[]::new)
        ).toArray(CustomSerializableClass[][]::new);
    }
    
    public static Set<CustomSerializableClass> getSet(ValuesProvider valuesProvider, int size) {
        CustomSerializableClass[] array = getArray(valuesProvider, size);
        return Arrays.stream(array).collect(Collectors.toSet());
    }
    
    public static Set<Set<CustomSerializableClass>> getNestedSet(ValuesProvider valuesProvider, int size1, int size2) {
        CustomSerializableClass[][] array = getNestedArray(valuesProvider, size1, size2);
        return Arrays.stream(array)
                .map(row -> Arrays.stream(row)
                        .collect(Collectors.toSet()))
                .collect(Collectors.toSet());
    }
    
    public static List<CustomSerializableClass> getList(ValuesProvider valuesProvider, int size) {
        CustomSerializableClass[] array = getArray(valuesProvider, size);
        return Arrays.stream(array).collect(Collectors.toList());
    }
    
    public static List<List<CustomSerializableClass>> getNestedList(ValuesProvider valuesProvider, int size1, int size2) {
        CustomSerializableClass[][] array = getNestedArray(valuesProvider, size1, size2);
        return Arrays.stream(array)
                .map(row -> Arrays.stream(row)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CustomSerializableClass{" +
                "serializableField=" + serializableField +
                ", nonSerializableField=" + nonSerializableField +
                '}';
    }
}
