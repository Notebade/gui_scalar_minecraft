package io.github.notebade.guiscalar.mixin;

import io.github.notebade.guiscalar.ScaleLayer;
import io.github.notebade.guiscalar.ScaleMath;
import io.github.notebade.guiscalar.ScaleSettings;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiMixin {
    @Inject(method = {"renderHotbar", "maybeRenderJumpMeter", "maybeRenderExperienceBar",
            "maybeRenderPlayerHealth", "maybeRenderVehicleHealth", "maybeRenderSelectedItemName",
            "maybeRenderSpectatorTooltip"}, at = @At("HEAD"))
    private void guiScalar$beginHotbar(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        graphics.pose().pushPose();
        var window = Minecraft.getInstance().getWindow();
        double requested = ScaleSettings.requested(ScaleLayer.HOTBAR);
        // An inherited hotbar follows the global scale, even while a container is open.
        if (requested == 0) requested = ScaleSettings.requested();
        if (requested == 0) {
            requested = window.calculateScale(Minecraft.getInstance().options.guiScale().get(),
                    Minecraft.getInstance().isEnforceUnicode());
        }
        float ratio = (float) (ScaleMath.effective(requested, window.getWidth(), window.getHeight())
                / window.getGuiScale());
        graphics.pose().translate(graphics.guiWidth() / 2, graphics.guiHeight(), 0);
        graphics.pose().scale(ratio, ratio, 1);
        graphics.pose().translate(-graphics.guiWidth() / 2, -graphics.guiHeight(), 0);
    }

    @Inject(method = {"renderHotbar", "maybeRenderJumpMeter", "maybeRenderExperienceBar",
            "maybeRenderPlayerHealth", "maybeRenderVehicleHealth", "maybeRenderSelectedItemName",
            "maybeRenderSpectatorTooltip"}, at = @At("RETURN"))
    private void guiScalar$endHotbar(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        graphics.pose().popPose();
    }
}
