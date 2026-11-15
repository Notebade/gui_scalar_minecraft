package io.github.notebade.guiscalar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class ScaleScreen extends Screen {
    private final Screen parent;
    private ScaleLayer layer = ScaleLayer.GLOBAL;
    private ScaleSlider slider;
    private EditBox input;
    private boolean dragging;
    private double anchorX, anchorWidth;

    public ScaleScreen(Screen parent) {
        super(Component.translatable("gui_scalar.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 140;
        slider = addRenderableWidget(new ScaleSlider(left, 48));
        input = new EditBox(font, left + 56, 80, 112, 20, Component.translatable("gui_scalar.exact"));
        input.setMaxLength(8);
        input.setValue(ScaleSettings.format(current()));
        addRenderableWidget(input);
        addRenderableWidget(Button.builder(Component.literal("-0.01"), b -> apply(current() - 0.01))
                .bounds(left, 80, 52, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+0.01"), b -> apply(current() + 0.01))
                .bounds(left + 172, 80, 52, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui_scalar.apply"), b -> applyInput())
                .bounds(left + 228, 80, 52, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(layer == ScaleLayer.GLOBAL
                ? "gui_scalar.vanilla" : "gui_scalar.inherit"), b -> {
            ScaleSettings.save(layer, 0);
            minecraft.resizeDisplay();
        }).bounds(left, 132, 280, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(left, 160, 280, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui_scalar.layer",
                Component.translatable("gui_scalar.layer." + layer.key)), b -> {
            layer = layer.next();
            rebuildWidgets();
        }).bounds(left, 190, 280, 20).build());
    }

    private double current() {
        double requested = ScaleSettings.requested(layer);
        return requested == 0 ? minecraft.getWindow().getGuiScale() : Math.min(maximum(), requested);
    }

    private double maximum() {
        var window = minecraft.getWindow();
        return Math.floor(ScaleMath.maximum(window.getWidth(), window.getHeight()) * 100) / 100;
    }

    private void apply(double value) {
        ScaleSettings.save(layer, Math.min(maximum(), ScaleMath.round(value)));
        minecraft.resizeDisplay();
    }

    private void applyInput() {
        try {
            double value = Double.parseDouble(input.getValue().replace(',', '.'));
            if (!Double.isFinite(value) || value < ScaleMath.MIN || value > maximum()) {
                input.setTextColor(0xFF5555);
                return;
            }
            apply(value);
        } catch (NumberFormatException e) {
            input.setTextColor(0xFF5555);
        }
    }

    @Override
    public void resize(Minecraft client, int newWidth, int newHeight) {
        if (!dragging) {
            super.resize(client, newWidth, newHeight);
            return;
        }
        // Keep the horizontal track under the mouse, but lay out every row at the new scale.
        // Reinitializing the screen here would discard the slider's mouse capture.
        width = newWidth;
        height = newHeight;
        int left = width / 2 - 140;
        int shift = left + 56 - input.getX();
        for (var child : children()) {
            if (child instanceof AbstractWidget widget && widget != slider) {
                widget.setX(widget.getX() + shift);
            }
        }
        double scale = minecraft.getWindow().getGuiScale();
        slider.setX((int) Math.round(anchorX / scale));
        slider.setWidth(Math.max(9, (int) Math.round(anchorWidth / scale)));
        input.setValue(ScaleSettings.format(current()));
    }

    @Override
    public void onClose() { minecraft.setScreen(parent); }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("gui_scalar.range", ScaleSettings.format(maximum())),
                width / 2, 110, 0xAAAAAA);
    }

    private final class ScaleSlider extends AbstractSliderButton {
        private final double max;

        private ScaleSlider(int x, int y) {
            super(x, y, 280, 20, Component.empty(), 0);
            max = maximum();
            value = max == 1 ? 0 : (Math.min(current(), max) - 1) / (max - 1);
            active = max > 1;
            updateMessage();
        }

        private double selected() { return ScaleMath.round(1 + value * (max - 1)); }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("gui_scalar.value", ScaleSettings.format(selected())));
        }

        @Override
        protected void applyValue() {
            if (dragging) {
                ScaleSettings.preview(layer, selected());
                minecraft.resizeDisplay();
            }
        }

        @Override
        public void onClick(double x, double y) {
            double scale = minecraft.getWindow().getGuiScale();
            anchorX = getX() * scale;
            anchorWidth = getWidth() * scale;
            dragging = true;
            super.onClick(x, y);
        }

        @Override
        public void onRelease(double x, double y) {
            super.onRelease(x, y);
            dragging = false;
            apply(selected());
        }

        @Override
        public boolean keyPressed(int key, int scanCode, int modifiers) {
            if (key == 263 || key == 262) {
                apply(selected() + (key == 262 ? 0.01 : -0.01));
                ScaleScreen.this.setFocused(slider);
                return true;
            }
            return super.keyPressed(key, scanCode, modifiers);
        }
    }
}
