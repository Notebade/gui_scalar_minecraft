package io.github.notebade.guiscalar;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = "gui_scalar", dist = Dist.CLIENT)
public final class GuiScalar {
    private final KeyMapping openScale = new KeyMapping(
            "key.gui_scalar.open", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, "key.categories.gui_scalar");

    public GuiScalar(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> new ScaleScreen(parent));
        modBus.addListener(this::registerKeys);
        NeoForge.EVENT_BUS.addListener(this::openScaleScreen);
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(openScale);
    }

    private void openScaleScreen(ClientTickEvent.Post event) {
        if (!openScale.consumeClick()) return;
        while (openScale.consumeClick()) {
            // Multiple queued presses still open only one screen.
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null && minecraft.level != null) {
            minecraft.setScreen(new ScaleScreen(null));
        }
    }
}
