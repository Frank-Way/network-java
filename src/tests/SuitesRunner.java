package tests;

import tests.cases.CaseWrapper;
import tests.suites.Suite;
import tests.suites.coding.binary.BinaryCoding;
import tests.suites.noise.Noise;
import tests.suites.serialization.yaml.YamlSerializationOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuitesRunner {
    private static Suite[] getSuites() {
        return new Suite[] {
//                new BinaryCoding(),
//                new Noise(),
//                new YamlSerializationOnly(),
        };
    }
    public static void main(String[] args) {
        final Suite[] suites = getSuites();
        for (Suite suite: suites) {
            final Map<TestStatus, List<String>> resultsMap = getNewResultsMap();
            System.out.println("Запуск сьюта: " + suite);
            for (int i = 0; i < suite.getCases().length; i++) {
                final CaseWrapper caseWrapper = suite.getCases()[i];
                final Object[] caseArgs = suite.getArgs()[i];
                System.out.println("Запуск кейса: " + caseWrapper);
                final TestStatus testStatus = caseWrapper.process(caseArgs);
                System.out.println("Результат запуска кейса " + caseWrapper + ": " + testStatus);
                resultsMap.get(testStatus).add(caseWrapper.toString());
            }
            System.out.println("Результаты запуска сьюта " + suite + ": " + resultsMap);
            System.out.println("Пройдено: " + resultsMap.get(TestStatus.PASSED).size());
            System.out.println("Провалено: " + resultsMap.get(TestStatus.FAILED).size());
            System.out.println("Сломано: " + resultsMap.get(TestStatus.BROKEN).size());
            System.out.println();
        }
    }

    private static Map<TestStatus, List<String>> getNewResultsMap() {
        final Map<TestStatus, List<String>> resultsMap = new HashMap<>();
        for (TestStatus testStatus: TestStatus.values())
            resultsMap.put(testStatus, new ArrayList<>());
        return resultsMap;
    }
}
