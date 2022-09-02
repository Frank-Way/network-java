package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

public class ArrayFieldsClass extends TestSerializableClass {
    // поля с массивами
    @YamlField protected int[] integerArrayField;
    @YamlField protected Integer[] integerArrayBoxedField;

    @YamlField protected double[] doubleArrayField;
    @YamlField protected Double[] doubleArrayBoxedField;

    @YamlField protected boolean[] booleanArrayField;
    @YamlField protected Boolean[] booleanArrayBoxedField;

    @YamlField protected String[] stringArrayField;

    @YamlField protected CustomSerializableClass[] customObjectArrayField;
//    @YamlField protected CustomNotSerializableClass[] customObjectArrayFieldNotSerializable;

//    public ArrayFieldsClass(int[] integerArrayField, Integer[] integerArrayBoxedField, double[] doubleArrayField, Double[] doubleArrayBoxedField, boolean[] booleanArrayField, Boolean[] booleanArrayBoxedField, String[] stringArrayField, CustomSerializableClass[] customObjectArrayField) {
//        this.integerArrayField = integerArrayField;
//        this.integerArrayBoxedField = integerArrayBoxedField;
//        this.doubleArrayField = doubleArrayField;
//        this.doubleArrayBoxedField = doubleArrayBoxedField;
//        this.booleanArrayField = booleanArrayField;
//        this.booleanArrayBoxedField = booleanArrayBoxedField;
//        this.stringArrayField = stringArrayField;
//        this.customObjectArrayField = customObjectArrayField;
//    }

    @Override
    public void set(ValuesProvider valuesProvider) {
        int size = getRandomSize();
        integerArrayField = valuesProvider.getArrayIntegers(size);
        integerArrayBoxedField = valuesProvider.getArrayIntegersBoxed(size);
        doubleArrayField = valuesProvider.getArrayDoubles(size);
        doubleArrayBoxedField = valuesProvider.getArrayDoublesBoxed(size);
        booleanArrayField = valuesProvider.getArrayBooleans(size);
        booleanArrayBoxedField = valuesProvider.getArrayBooleansBoxed(size);
        stringArrayField = valuesProvider.getArrayStrings(size);
        customObjectArrayField = CustomSerializableClass.getArray(valuesProvider, size);
    }

    @Override
    public String toString() {
        return "ArrayFieldsClass{" +
//                "integerArrayField=" + Arrays.toString(integerArrayField) +
//                ", integerArrayBoxedField=" + Arrays.toString(integerArrayBoxedField) +
//                ", doubleArrayField=" + Arrays.toString(doubleArrayField) +
//                ", doubleArrayBoxedField=" + Arrays.toString(doubleArrayBoxedField) +
//                ", booleanArrayField=" + Arrays.toString(booleanArrayField) +
//                ", booleanArrayBoxedField=" + Arrays.toString(booleanArrayBoxedField) +
//                ", stringArrayField=" + Arrays.toString(stringArrayField) +
//                ", customObjectArrayField=" + Arrays.toString(customObjectArrayField) +
                '}';
    }
}
