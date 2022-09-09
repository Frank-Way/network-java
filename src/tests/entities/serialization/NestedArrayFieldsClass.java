package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

public class NestedArrayFieldsClass extends TestSerializableClass {
    // поля с двумерными массивами
    @YamlField protected int[][] integerNestedArrayField;
    @YamlField protected Integer[][] integerNestedArrayBoxedField;

    @YamlField protected double[][] doubleNestedArrayField;
    @YamlField protected Double[][] doubleNestedArrayBoxedField;

    @YamlField protected boolean[][] booleanNestedArrayField;
    @YamlField protected Boolean[][] booleanNestedArrayBoxedField;

    @YamlField protected String[][] stringNestedArrayField;

    @YamlField protected CustomSerializableClass[][] customObjectNestedArrayField;

    @Override
    public void set(ValuesProvider valuesProvider) {
        int size1 = getRandomSize();
        int size2 = getRandomSize();
        integerNestedArrayField = valuesProvider.getNestedArrayIntegers(size1, size2);
        integerNestedArrayBoxedField = valuesProvider.getNestedArrayIntegersBoxed(size1, size2);

        doubleNestedArrayField = valuesProvider.getNestedArrayDoubles(size1, size2);
        doubleNestedArrayBoxedField = valuesProvider.getNestedArrayDoublesBoxed(size1, size2);

        booleanNestedArrayField = valuesProvider.getNestedArrayBooleans(size1, size2);
        booleanNestedArrayBoxedField = valuesProvider.getNestedArrayBooleansBoxed(size1, size2);

        stringNestedArrayField = valuesProvider.getNestedArrayStrings(size1, size2);
        customObjectNestedArrayField = CustomSerializableClass.getNestedArray(valuesProvider, size1, size2);
    }

    @Override
    public String toString() {
        return "NestedArrayFieldsClass{" +
//                "integerNestedArrayField=" + Arrays.deepToString(integerNestedArrayField) +
//                ", integerNestedArrayBoxedField=" + Arrays.deepToString(integerNestedArrayBoxedField) +
//                ", doubleNestedArrayField=" + Arrays.deepToString(doubleNestedArrayField) +
//                ", doubleNestedArrayBoxedField=" + Arrays.deepToString(doubleNestedArrayBoxedField) +
//                ", booleanNestedArrayField=" + Arrays.deepToString(booleanNestedArrayField) +
//                ", booleanNestedArrayBoxedField=" + Arrays.deepToString(booleanNestedArrayBoxedField) +
//                ", stringNestedArrayField=" + Arrays.deepToString(stringNestedArrayField) +
//                ", customObjectNestedArrayField=" + Arrays.deepToString(customObjectNestedArrayField) +
                '}';
    }
}
