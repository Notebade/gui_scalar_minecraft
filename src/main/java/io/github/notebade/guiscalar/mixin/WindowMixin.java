package io.github.notebade.guiscalar.mixin;

import com.mojang.blaze3d.platform.Window;
import io.github.notebade.guiscalar.ScaleMath;
import io.github.notebade.guiscalar.ScaleSettings;
import io.github.notebade.guiscalar.ScaleLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Window.class)
abstract class WindowMixin {
    @Shadow public abstract int getWidth();
    @Shadow public abstract int getHeight();

    @ModifyVariable(method = "setGuiScale", at = @At("HEAD"), argsOnly = true)
    private double guiScalar$useFractionalScale(double vanilla) {
        double requested = ScaleSettings.requested();
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?>
                && ScaleSettings.requested(ScaleLayer.CONTAINER) != 0) {
            requested = ScaleSettings.requested(ScaleLayer.CONTAINER);
        }
        return requested == 0 ? vanilla : ScaleMath.effective(requested, getWidth(), getHeight());
    }
}
