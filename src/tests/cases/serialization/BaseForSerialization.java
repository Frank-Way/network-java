package tests.cases.serialization;

import serialization.YamlSerializationUtils;
import serialization.exceptions.SerializationException;
import serialization.serializers.Serializer;
import tests.TestStatus;
import tests.cases.CaseWrapper;
import tests.entities.serialization.ArrayFieldsClass;
import tests.entities.serialization.TestSerializableClass;
import tests.utils.ValuesProvider;
import utils.Utils;

import java.util.*;
import java.util.logging.Logger;

public abstract class BaseForSerialization extends CaseWrapper {
    
    protected final static String doubleFormat = "%13.8f";

    protected String[] getIdsByStatus(Map<CaseWrapper, TestStatus> testStatusMap, TestStatus status) {
        return testStatusMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(status))
                .map(Map.Entry::getKey).map(CaseWrapper::getId)
                .toArray(String[]::new);
    }

    protected String[] getArgsByStatus(Map<String, TestStatus> argsStatusMap, TestStatus status) {
        return argsStatusMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(status))
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }

    protected abstract Object[][] getArgs();

    protected static String argsToString(TestSerializableClass testSerializableClassInstance, ValuesProvider valuesProvider, Boolean isDeserializationRequired, Serializer serializer) {
        return "{testSerializableClassInstance: " + testSerializableClassInstance +
                ", valuesProvider: " + valuesProvider +
                ", isDeserializationRequired: " + isDeserializationRequired +
                ", serializer: " + serializer + "}";
    }

    public TestStatus innerTest(TestSerializableClass testSerializableClassInstance, ValuesProvider valuesProvider, Boolean isDeserializationRequired, Serializer serializer) {
        System.out.println(new Date() + "    " + "start innerTest");
        System.out.println(new Date() + "    " + argsToString(testSerializableClassInstance, valuesProvider, isDeserializationRequired, serializer));
        testSerializableClassInstance.set(valuesProvider);
        System.out.println(new Date() + "    " + "testSerializableClassInstance: " + testSerializableClassInstance);
        byte[] testSerializableClassSerialized = null;
        try {
            testSerializableClassSerialized = serializer.serialize(testSerializableClassInstance);
        } catch (SerializationException se) {
            System.out.println(new Date() + "    " + se.getMessage());
            return TestStatus.FAILED;
        }
        System.out.println(new Date() + "    " + "testSerializableClassSerialized:\n" + serializedToString(testSerializableClassSerialized));
        if (isDeserializationRequired) {
            TestSerializableClass testSerializableClassDeserialized = null;
            try {
                testSerializableClassDeserialized = (TestSerializableClass) serializer.deserialize(
                        testSerializableClassSerialized, testSerializableClassInstance.getClass());
            } catch (SerializationException se) {
                System.out.println(new Date() + "    " + se.getMessage());
                return TestStatus.FAILED;
            }
            System.out.println(new Date() + "    " + "testSerializableClassDeserialized: " + testSerializableClassDeserialized);

            boolean isEquals = YamlSerializationUtils.isEqualsYamlSerializable(testSerializableClassInstance,
                    testSerializableClassDeserialized);
//            byte[] serializedAgain;
//            try {
//                serializedAgain = serializer.serialize(testSerializableClassDeserialized);
//                isEquals = Arrays.equals(testSerializableClassSerialized, serializedAgain);
//            } catch (SerializationException se) {
//                System.out.println(new Date() + "    " + se.getMessage());
//                return TestStatus.FAILED;
//            }
            System.out.println(new Date() + "    " + "isEquals: " + isEquals);
            if (!isEquals) {
                System.out.println(new Date() + "    " + "Объекты не совпали\ntestSerializableClassInstance: " +
                        testSerializableClassInstance + "\ntestSerializableClassDeserialized: " +
                        testSerializableClassDeserialized);
                return TestStatus.FAILED;
            }
            else {
                System.out.println(new Date() + "    " + "Объекты совпали");
                return TestStatus.PASSED;
            }
        }
        return TestStatus.PASSED;
    }
    
    protected abstract String serializedToString(byte[] source);
    protected abstract ValuesProvider getValuesProvider();
    protected abstract Serializer getSerializer();

    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public TestStatus process(Object... args) {
        Object[][] testArgs = getArgs();
        int count = testArgs.length;
        TestStatus[] statuses = new TestStatus[count];
        Map<String, TestStatus> argsStatusMap = new HashMap<>();
        for (int i = 0; i < count; i++) {
            Object[] arg = testArgs[i];
            TestSerializableClass testSerializableClassInstance = (TestSerializableClass) arg[0];
            ValuesProvider valuesProvider = (ValuesProvider) arg[1];
            Boolean isDeserializationRequired = (Boolean) arg[2];
            Serializer serializer = (Serializer) arg[3];
            try {
                statuses[i] = innerTest(testSerializableClassInstance, valuesProvider, isDeserializationRequired, serializer);
            } catch (Exception e) {
                statuses[i] = TestStatus.BROKEN;
            }
            argsStatusMap.put(argsToString(testSerializableClassInstance, valuesProvider, isDeserializationRequired, serializer), statuses[i]);
        }
        String[] passedArgs = getArgsByStatus(argsStatusMap, TestStatus.PASSED);
        String[] failedArgs = getArgsByStatus(argsStatusMap, TestStatus.FAILED);
        String[] brokenArgs = getArgsByStatus(argsStatusMap, TestStatus.BROKEN);
        System.out.println(new Date() + "    " + "Тестов успешно прошло: " + passedArgs.length + ", провалилось: " + failedArgs.length +
                ", сломалось: " + brokenArgs.length);
        if (failedArgs.length > 0)
            System.out.println(new Date() + "    " + "Проваленные тесты: " + Arrays.toString(failedArgs));
        if (brokenArgs.length > 0)
            System.out.println(new Date() + "    " + "Сломанные тесты: " + Arrays.toString(brokenArgs));
        return failedArgs.length + brokenArgs.length == 0 ? TestStatus.PASSED : TestStatus.FAILED;
    }
}
