package io.github.notebade.guiscalar;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.EnumMap;
import net.neoforged.fml.loading.FMLPaths;

public final class ScaleSettings {
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("gui-scalar.properties");
    // Zero delegates to Minecraft's existing Auto/integer setting until the user opts in.
    private static final EnumMap<ScaleLayer, Double> values = load();

    private ScaleSettings() {}

    public static double requested() { return requested(ScaleLayer.GLOBAL); }

    public static double requested(ScaleLayer layer) { return values.get(layer); }

    public static String format(double value) { return String.format(Locale.ROOT, "%.2f", value); }

    public static void preview(double value) { preview(ScaleLayer.GLOBAL, value); }

    public static void preview(ScaleLayer layer, double value) {
        values.put(layer, value == 0 ? 0 : ScaleMath.round(value));
    }

    public static void save(double value) {
        save(ScaleLayer.GLOBAL, value);
    }

    public static void save(ScaleLayer layer, double value) {
        preview(layer, value);
        flush();
    }

    static void flush() {
        Properties properties = new Properties();
        for (ScaleLayer layer : ScaleLayer.values()) {
            properties.setProperty(layer.key, format(requested(layer)));
        }
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                properties.store(writer, "GUI Scalar: 0 = inherit (global: vanilla); otherwise absolute GUI scale");
            }
        } catch (IOException e) {
            LogUtils.getLogger().error("Cannot save GUI Scalar settings", e);
        }
    }

    private static EnumMap<ScaleLayer, Double> load() {
        EnumMap<ScaleLayer, Double> result = new EnumMap<>(ScaleLayer.class);
        for (ScaleLayer layer : ScaleLayer.values()) result.put(layer, 0.0);
        if (!Files.exists(FILE)) return result;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Properties properties = new Properties();
            properties.load(reader);
            for (ScaleLayer layer : ScaleLayer.values()) {
                try {
                    double value = Double.parseDouble(properties.getProperty(layer.key, "0"));
                    result.put(layer, value == 0 ? 0 : ScaleMath.round(value));
                } catch (NumberFormatException e) {
                    LogUtils.getLogger().warn("Invalid GUI Scalar setting: {}", layer.key);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.getLogger().warn("Invalid GUI Scalar settings; using vanilla scale", e);
        }
        return result;
    }
}
