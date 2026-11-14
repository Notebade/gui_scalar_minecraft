package io.github.notebade.guiscalar;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import net.neoforged.fml.loading.FMLPaths;

public final class ScaleSettings {
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("gui-scalar.properties");
    // Zero delegates to Minecraft's existing Auto/integer setting until the user opts in.
    private static double requested = load();

    private ScaleSettings() {}

    public static double requested() { return requested; }

    public static String format(double value) { return String.format(Locale.ROOT, "%.2f", value); }

    public static void preview(double value) { requested = value == 0 ? 0 : ScaleMath.round(value); }

    public static void save(double value) {
        preview(value);
        flush();
    }

    static void flush() {
        Properties properties = new Properties();
        properties.setProperty("scale", format(requested));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                properties.store(writer, "GUI Scalar: 0 = vanilla; otherwise 1.00 to 16.00");
            }
        } catch (IOException e) {
            LogUtils.getLogger().error("Cannot save GUI Scalar settings", e);
        }
    }

    private static double load() {
        if (!Files.exists(FILE)) return 0;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Properties properties = new Properties();
            properties.load(reader);
            double value = Double.parseDouble(properties.getProperty("scale", "0"));
            return value == 0 ? 0 : ScaleMath.round(value);
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.getLogger().warn("Invalid GUI Scalar settings; using vanilla scale", e);
            return 0;
        }
    }
}
