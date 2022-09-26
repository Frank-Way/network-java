package tests;

import tests.cases.CaseWrapper;
import tests.suites.Suite;
import tests.suites.serialization.yaml.YamlSerialization;

import java.util.*;

public class SuitesRunner {
    private static Suite[] getSuites() {
        return new Suite[] {
//                new BinaryCoding(),
//                new Noise(),
                new YamlSerialization(),
        };
    }
    public static void main(String[] args) {
        final Suite[] suites = getSuites();
        for (Suite suite: suites) {
            final Map<TestStatus, List<String>> resultsMap = getNewResultsMap();
            System.out.println(new Date() + "    " + "Запуск сьюта: " + suite);
            for (int i = 0; i < suite.getCases().length; i++) {
                final CaseWrapper caseWrapper = suite.getCases()[i];
                final Object[] caseArgs = suite.getArgs()[i];
                System.out.println(new Date() + "    " + "Запуск кейса: " + caseWrapper);
                final TestStatus testStatus = caseWrapper.process(caseArgs);
                System.out.println(new Date() + "    " + "Результат запуска кейса " + caseWrapper + ": " + testStatus);
                resultsMap.get(testStatus).add(caseWrapper.toString());
            }
            System.out.println(new Date() + "    " + "Результаты запуска сьюта " + suite + ": " + resultsMap);
            System.out.println(new Date() + "    " + "Пройдено: " + resultsMap.get(TestStatus.PASSED).size());
            System.out.println(new Date() + "    " + "Провалено: " + resultsMap.get(TestStatus.FAILED).size());
            for (String caseWrapper: resultsMap.get(TestStatus.FAILED))
                System.out.println(caseWrapper);
            System.out.println(new Date() + "    " + "Сломано: " + resultsMap.get(TestStatus.BROKEN).size());
            for (String caseWrapper: resultsMap.get(TestStatus.BROKEN))
                System.out.println(caseWrapper);
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
