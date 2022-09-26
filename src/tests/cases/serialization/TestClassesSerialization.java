package tests.cases.serialization;

import serialization.serializers.Serializer;
import tests.TestStatus;
import tests.entities.serialization.*;
import tests.utils.ValuesProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestClassesSerialization extends BaseForSerialization {
    @Override
    public String getDescription() {
        return "Проверка сериализации классов: [" + convertObjectsToJoinedClassNames(getTestObjects()) + " ]";
    }

    @Override
    public TestStatus process(Object... args) {
        final ValuesProvider valuesProvider = (ValuesProvider) args[0];
        final Serializer serializer = (Serializer) args[1];
        final boolean isDeserializationRequired = (boolean) args[2];

        final TestSerializableClass[] testObjects = getTestObjects();
        setValues(testObjects, valuesProvider);
        for (TestSerializableClass testObject: testObjects) {
            final TestStatus testStatus = innerTest(testObject, isDeserializationRequired, serializer);
            if (testStatus != TestStatus.PASSED)
                return testStatus;
        }
        return TestStatus.PASSED;
    }

    private TestSerializableClass[] getTestObjects() {
        return new TestSerializableClass[]{new SimpleFieldsClass(),
                new ArrayFieldsClass(),
                new NestedArrayFieldsClass(),
                new ListFieldClass(),
                new NestedListFieldsClass(),
                new SetFieldsClass(),
                new NestedSetFieldsClass(),
                new SimpleMapFieldsClass(),
                new ComplexMapFieldsClass()};
    }

    private void setValues(TestSerializableClass[] testObjects, ValuesProvider valuesProvider) {
        Arrays.stream(testObjects).forEach(testObject -> testObject.set(valuesProvider));
    }
}
