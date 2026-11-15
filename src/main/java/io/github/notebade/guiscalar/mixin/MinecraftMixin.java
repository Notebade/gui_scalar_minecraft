package io.github.notebade.guiscalar.mixin;

import io.github.notebade.guiscalar.ScaleLayer;
import io.github.notebade.guiscalar.ScaleSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Inject(method = "setScreen", at = @At("RETURN"))
    private void guiScalar$updateContainerScale(Screen screen, CallbackInfo ci) {
        if (ScaleSettings.requested(ScaleLayer.CONTAINER) != 0) {
            ((Minecraft) (Object) this).resizeDisplay();
        }
    }
}
