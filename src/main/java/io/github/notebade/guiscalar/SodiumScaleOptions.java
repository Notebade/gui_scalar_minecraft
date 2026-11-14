package io.github.notebade.guiscalar;

import com.mojang.blaze3d.platform.Window;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class SodiumScaleOptions implements ConfigEntryPoint {
    private static final int VANILLA = 99;

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        var slider = builder.createIntegerOption(ResourceLocation.parse("gui_scalar:scale"))
                .setName(Component.translatable("options.guiScale"))
                .setTooltip(Component.translatable("gui_scalar.sodium.tooltip"))
                .setDefaultValue(VANILLA)
                .setRangeProvider(state -> range(), ConfigState.UPDATE_ON_REBUILD, ConfigState.UPDATE_ON_APPLY)
                .setValueFormatter(value -> value == VANILLA
                        ? Component.translatable("gui_scalar.sodium.vanilla")
                        : Component.literal(ScaleSettings.format(value / 100.0)))
                .setBinding(value -> ScaleSettings.preview(value == VANILLA ? 0 : value / 100.0),
                        SodiumScaleOptions::readScale)
                .setStorageHandler(ScaleSettings::flush)
                .setApplyHook(state -> Minecraft.getInstance().resizeDisplay());

        builder.registerOwnModOptions().registerOptionReplacement(
                ResourceLocation.parse("sodium:general.gui_scale"), slider);
    }

    private static Range range() {
        Window window = Minecraft.getInstance().getWindow();
        int maximum = (int) Math.floor(100 * ScaleMath.maximum(window.getWidth(), window.getHeight()));
        return new Range(VANILLA, Math.max(maximum, readScale()), 1);
    }

    private static int readScale() {
        double requested = ScaleSettings.requested();
        return requested == 0 ? VANILLA : (int) Math.round(requested * 100);
    }

}
