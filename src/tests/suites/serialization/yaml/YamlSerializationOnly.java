package tests.suites.serialization.yaml;

import tests.cases.CaseWrapper;
import tests.cases.serialization.yaml.random.RandomSerializationWrite;
import tests.cases.serialization.yaml.random.RandomSerializationWriteRead;

import java.util.Date;

public class YamlSerializationOnly {
    
    public static void main(String[] args) {
        CaseWrapper[] tests = new CaseWrapper[] {
                new RandomSerializationWrite(),
                new RandomSerializationWriteRead(),
        };
        for (CaseWrapper test: tests)
            try {
                test.process();
            } catch (Exception e) {
                System.out.println(new Date() + "    " + "Неожиданное исключение во время выполнения: " + test.getId());
            }
    }
}
