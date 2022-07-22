package options;

public abstract class OverallConstants {
    public final static boolean DEBUG_MODE = false;

    public final static String DOUBLE_FORMAT = "%20.15f";

    public final static int TABLE_PART_PERCENTS = 10;

    public final static boolean PRINT_REQUIRED = true;

    public final static PrintOptions PRINT_EACH_CONFIGURATION = new PrintOptions(
            false, false, false, false);

    public final static PrintOptions PRINT_EACH_CONFIGURATION_BEST = new PrintOptions(
            false, false, false, false);

    public final static PrintOptions PRINT_EXPERIMENT_BEST = new PrintOptions(
            false, false, false, false);

    public final static PrintOptions PRINT_ALL_EXPERIMENTS_BEST = new PrintOptions(
            true, true, true, true);

}
