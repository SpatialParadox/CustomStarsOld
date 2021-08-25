package condolence.customstars.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class CustomStarsConfig {
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_COLOR = "color";
    private static final String CATEGORY_END = "end";

    public static Config CONFIG;
    public static ForgeConfigSpec CONFIG_SPEC;

    public static boolean useCustomStars;
    public static double baseSize;
    public static double maxSizeMultiplier;
    public static int starCount;
    public static double noiseThreshold;
    public static int noisePercentage;

    public static int red;
    public static int green;
    public static int blue;
    public static double alpha;

    public static boolean updated = false;

    static {
        Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        CONFIG = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    /**
     * Sets each config entry's respective field to the current loaded value.
     * This prevents a ConfigValue#get invocation every time a config value is requested.
     */
    public static void bakeConfig() {
        useCustomStars = CONFIG.useCustomStars.get();
        baseSize = CONFIG.baseSize.get();
        maxSizeMultiplier = CONFIG.maxSizeMultiplier.get();
        starCount = CONFIG.starCount.get();
        noiseThreshold = CONFIG.noiseThreshold.get();
        noisePercentage = CONFIG.noisePercentage.get();

        red = CONFIG.redColor.get();
        green = CONFIG.greenColor.get();
        blue = CONFIG.blueColor.get();
        alpha = CONFIG.alpha.get();
    }

    public static void onLoad(ModConfig.ModConfigEvent event) {
        bakeConfig();
        updated = true;
    }

    public static void validateUpdate() {
        updated = false;
    }

    private static class Config {
        public final ForgeConfigSpec.BooleanValue useCustomStars;
        public final ForgeConfigSpec.DoubleValue baseSize;
        public final ForgeConfigSpec.DoubleValue maxSizeMultiplier;
        public final ForgeConfigSpec.IntValue starCount;
        public final ForgeConfigSpec.DoubleValue noiseThreshold;
        public final ForgeConfigSpec.IntValue noisePercentage;

        public final ForgeConfigSpec.IntValue redColor;
        public final ForgeConfigSpec.IntValue greenColor;
        public final ForgeConfigSpec.IntValue blueColor;
        public final ForgeConfigSpec.DoubleValue alpha;

        public Config(ForgeConfigSpec.Builder builder) {
            builder.push(CATEGORY_GENERAL);
            useCustomStars = builder.comment("Whether to use custom stars in the overworld/overworld-like dimensions").define("use_custom_stars", true);
            baseSize = builder.comment("Base size of stars").defineInRange("base_size", 0.15, 0.0, 1.0);
            maxSizeMultiplier = builder.comment("Max size multiplier").defineInRange("max_size_multiplier", 0.1, 0.0, 1.0);
            starCount = builder.comment("Number of stars to display").defineInRange("star_count", 1500, 0, 50000);
            noiseThreshold = builder.comment("Noise threshold").defineInRange("noise_threshold", 0.5, 0, 1);
            noisePercentage = builder.comment("Noise percentage").defineInRange("noise_percentage", 50, 0, 100);

            builder.push(CATEGORY_COLOR);
            redColor = builder.defineInRange("red", 255, 0, 255);
            greenColor = builder.defineInRange("green", 255, 0, 255);
            blueColor = builder.defineInRange("blue", 255, 0, 255);
            alpha = builder.defineInRange("alpha", 1.0, 0.0, 100.0);
            builder.pop(2);
        }
    }
}
