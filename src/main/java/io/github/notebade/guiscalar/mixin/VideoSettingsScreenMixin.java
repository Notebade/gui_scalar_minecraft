package io.github.notebade.guiscalar.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VideoSettingsScreen.class)
abstract class VideoSettingsScreenMixin {
    // Vanilla casts the GUI scale widget to CycleButton when Ctrl-scrolling.
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void guiScalar$preventVanillaCycleCast(double x, double y, double horizontal,
            double vertical, CallbackInfoReturnable<Boolean> cir) {
        if (Screen.hasControlDown()) cir.setReturnValue(false);
    }
}
