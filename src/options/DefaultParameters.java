package options;

public abstract class DefaultParameters {
    public static final int RETRIES = 1;
    public static final int SIZE = 1024;
    public static final double TEST_PART = 0.5;
    public static final double VALID_PART = 0.25;
    public static final double EXTENDING_FACTOR = 1.15;
    public static final int QUERIES = 10;
    public static final int EPOCHS = 1000;
    public static final int BATCH_SIZE = 64;
    public static final boolean EARLY_STOPPING = true;
    public static final double START_LR = 0.1;
    public static final double STOP_LR = 0.001;
}
