package options;

public class PrintOptions {
    private final boolean required;
    private final boolean parametersRequired;
    private final boolean tableRequired;
    private final boolean dynamicRequired;

    public PrintOptions(boolean required, boolean parametersRequired, boolean tableRequired, boolean dynamicRequired) {
        this.required = required;
        this.parametersRequired = parametersRequired;
        this.tableRequired = tableRequired;
        this.dynamicRequired = dynamicRequired;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isParametersRequired() {
        return parametersRequired;
    }

    public boolean isTableRequired() {
        return tableRequired;
    }

    public boolean isDynamicRequired() {
        return dynamicRequired;
    }
}
