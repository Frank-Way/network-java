package options;

import java.io.File;

public abstract class Constants {
    public final static boolean DEBUG_MODE = false;

    public final static String DOUBLE_FORMAT = "%25.20f";

    public final static double TABLE_PART = 0.1;

    public final static boolean PRINT_REQUIRED = true;

    public final static PrintOptions PRINT_EACH_CONFIGURATION = new PrintOptions(true);

    public final static PrintOptions PRINT_EACH_CONFIGURATION_BEST = new PrintOptions(false);

    public final static PrintOptions PRINT_EXPERIMENT_BEST = new PrintOptions(true);

    public final static PrintOptions PRINT_ALL_EXPERIMENTS_BEST = new PrintOptions(false);

    public final static boolean SAVE_REQUIRED = true;

    public final static String SAVE_FOLDER = System.getProperty("user.dir") + File.separator + "networks";
    public final static String SAVE_NETWORK_PATTERN = "network_%s.dat";

    public final static boolean SAVE_EACH_CONFIGURATION = false;

    public final static boolean SAVE_EACH_CONFIGURATION_BEST = false;

    public final static boolean SAVE_EXPERIMENT_BEST = true;

    public final static boolean SAVE_ALL_EXPERIMENTS_BEST = false;

}
