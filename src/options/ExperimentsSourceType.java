package options;

/**
 * Тип источника описания экспериментов
 */
public enum ExperimentsSourceType {
    /**
     * Из кода - Experiments.getDefaultExperimentConfigurations
     */
    CODE,

    /**
     * Из файла в формате YAML
     */
    YAML_FILE
}
