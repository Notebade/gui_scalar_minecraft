package io.github.notebade.guiscalar;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class SodiumScaleOptions implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        var control = builder.createExternalButtonOption(ResourceLocation.parse("gui_scalar:scale"))
                .setName(Component.translatable("gui_scalar.open"))
                .setTooltip(Component.translatable("gui_scalar.sodium.tooltip"))
                .setScreenConsumer(parent -> Minecraft.getInstance().setScreen(new ScaleScreen(parent)));

        builder.registerOwnModOptions().registerOptionReplacement(
                ResourceLocation.parse("sodium:general.gui_scale"), control);
    }
}
