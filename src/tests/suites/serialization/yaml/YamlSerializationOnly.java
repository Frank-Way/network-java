package tests.suites.serialization.yaml;

import tests.cases.CaseWrapper;
import tests.cases.serialization.yaml.BaseForSerializationYaml;
import tests.cases.serialization.yaml.random.RandomSerializationWrite;
import tests.cases.serialization.yaml.random.RandomSerializationWriteRead;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
