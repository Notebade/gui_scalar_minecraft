package io.github.notebade.guiscalar.mixin;

import io.github.notebade.guiscalar.ScaleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionInstance.class)
abstract class OptionInstanceMixin {
    @Inject(method = "createButton(Lnet/minecraft/client/Options;IIILjava/util/function/Consumer;)Lnet/minecraft/client/gui/components/AbstractWidget;",
            at = @At("HEAD"), cancellable = true)
    private void guiScalar$replaceScaleControl(Options options, int x, int y, int width,
            Consumer<?> changed, CallbackInfoReturnable<AbstractWidget> cir) {
        if ((Object) this != options.guiScale()) return;
        cir.setReturnValue(Button.builder(Component.translatable("gui_scalar.open"), button -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new ScaleScreen(minecraft.screen));
        }).bounds(x, y, width, 20).build());
    }
}
