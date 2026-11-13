package io.github.notebade.guiscalar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class ScaleScreen extends Screen {
    private final Screen parent;
    private ScaleSlider slider;
    private EditBox input;
    private boolean dragging;
    private double anchorX, anchorY, anchorWidth, anchorHeight;

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
        addRenderableWidget(Button.builder(Component.translatable("gui_scalar.vanilla"), b -> {
            ScaleSettings.save(0);
            minecraft.resizeDisplay();
        }).bounds(left, 132, 280, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(left, 160, 280, 20).build());
    }

    private double current() { return minecraft.getWindow().getGuiScale(); }

    private double maximum() {
        var window = minecraft.getWindow();
        return Math.floor(ScaleMath.maximum(window.getWidth(), window.getHeight()) * 100) / 100;
    }

    private void apply(double value) {
        ScaleSettings.save(Math.min(maximum(), ScaleMath.round(value)));
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
        // Preserve mouse capture and the slider's physical position during live scaling.
        width = newWidth;
        height = newHeight;
        double scale = current();
        slider.setX((int) Math.round(anchorX / scale));
        slider.setY((int) Math.round(anchorY / scale));
        slider.setWidth(Math.max(9, (int) Math.round(anchorWidth / scale)));
        slider.setHeight(Math.max(1, (int) Math.round(anchorHeight / scale)));
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
                ScaleSettings.preview(selected());
                minecraft.resizeDisplay();
            }
        }

        @Override
        public void onClick(double x, double y) {
            double scale = current();
            anchorX = getX() * scale;
            anchorY = getY() * scale;
            anchorWidth = getWidth() * scale;
            anchorHeight = getHeight() * scale;
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
