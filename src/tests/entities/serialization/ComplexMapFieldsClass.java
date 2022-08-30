package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

import java.util.HashMap;

public class ComplexMapFieldsClass extends TestSerializableClass {
    // поля с ассоциативными списками, где ключ или значение - кастомный объект
    @YamlField protected HashMap<Integer, CustomSerializableClass> integerObjectMapBoxedField;
    @YamlField protected HashMap<CustomSerializableClass, Integer> objectIntegerMapBoxedField;

    @YamlField protected HashMap<Double, CustomSerializableClass> doubleObjectMapBoxedField;
    @YamlField protected HashMap<CustomSerializableClass, Double> objectDoubleMapBoxedField;

    @YamlField protected HashMap<String, CustomSerializableClass> stringObjectMapBoxedField;
    @YamlField protected HashMap<CustomSerializableClass, String> objectStringMapBoxedField;

    @YamlField protected HashMap<Boolean, CustomSerializableClass> booleanObjectMapBoxedField;
    @YamlField protected HashMap<CustomSerializableClass, Boolean> objectBooleanMapBoxedField;

//    public ComplexMapFieldsClass(HashMap<Integer, CustomSerializableClass> integerObjectMapBoxedField, HashMap<CustomSerializableClass, Integer> objectIntegerMapBoxedField, HashMap<Double, CustomSerializableClass> doubleObjectMapBoxedField, HashMap<CustomSerializableClass, Double> objectDoubleMapBoxedField, HashMap<String, CustomSerializableClass> stringObjectMapBoxedField, HashMap<CustomSerializableClass, String> objectStringMapBoxedField, HashMap<Boolean, CustomSerializableClass> booleanObjectMapBoxedField, HashMap<CustomSerializableClass, Boolean> objectBooleanMapBoxedField) {
//        this.integerObjectMapBoxedField = integerObjectMapBoxedField;
//        this.objectIntegerMapBoxedField = objectIntegerMapBoxedField;
//        this.doubleObjectMapBoxedField = doubleObjectMapBoxedField;
//        this.objectDoubleMapBoxedField = objectDoubleMapBoxedField;
//        this.stringObjectMapBoxedField = stringObjectMapBoxedField;
//        this.objectStringMapBoxedField = objectStringMapBoxedField;
//        this.booleanObjectMapBoxedField = booleanObjectMapBoxedField;
//        this.objectBooleanMapBoxedField = objectBooleanMapBoxedField;
//    }


    @Override
    public void set(ValuesProvider valuesProvider) {
        int size = getRandomSize();
        integerObjectMapBoxedField = (HashMap<Integer, CustomSerializableClass>) valuesProvider.getMap(valuesProvider.getArrayIntegersBoxed(size), CustomSerializableClass.getArray(valuesProvider, size));
        objectIntegerMapBoxedField = (HashMap<CustomSerializableClass, Integer>) valuesProvider.getMap(CustomSerializableClass.getArray(valuesProvider, size), valuesProvider.getArrayIntegersBoxed(size));

        doubleObjectMapBoxedField = (HashMap<Double, CustomSerializableClass>) valuesProvider.getMap(valuesProvider.getArrayDoublesBoxed(size), CustomSerializableClass.getArray(valuesProvider, size));
        objectDoubleMapBoxedField = (HashMap<CustomSerializableClass, Double>) valuesProvider.getMap(CustomSerializableClass.getArray(valuesProvider, size), valuesProvider.getArrayDoublesBoxed(size));

        stringObjectMapBoxedField = (HashMap<String, CustomSerializableClass>) valuesProvider.getMap(valuesProvider.getArrayStrings(size), CustomSerializableClass.getArray(valuesProvider, size));
        objectStringMapBoxedField = (HashMap<CustomSerializableClass, String>) valuesProvider.getMap(CustomSerializableClass.getArray(valuesProvider, size), valuesProvider.getArrayStrings(size));

        booleanObjectMapBoxedField = (HashMap<Boolean, CustomSerializableClass>) valuesProvider.getMap(valuesProvider.getArrayBooleansBoxed(size), CustomSerializableClass.getArray(valuesProvider, size));
        objectBooleanMapBoxedField = (HashMap<CustomSerializableClass, Boolean>) valuesProvider.getMap(CustomSerializableClass.getArray(valuesProvider, size), valuesProvider.getArrayBooleansBoxed(size));
    }

    @Override
    public String toString() {
        return "ComplexMapFieldsClass{" +
//                "integerObjectMapBoxedField=" + integerObjectMapBoxedField +
//                ", objectIntegerMapBoxedField=" + objectIntegerMapBoxedField +
//                ", doubleObjectMapBoxedField=" + doubleObjectMapBoxedField +
//                ", objectDoubleMapBoxedField=" + objectDoubleMapBoxedField +
//                ", stringObjectMapBoxedField=" + stringObjectMapBoxedField +
//                ", objectStringMapBoxedField=" + objectStringMapBoxedField +
//                ", booleanObjectMapBoxedField=" + booleanObjectMapBoxedField +
//                ", objectBooleanMapBoxedField=" + objectBooleanMapBoxedField +
                '}';
    }
}
