package com.evandev.remi.gui.components;

import com.evandev.ReliableEmi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Switch extends AbstractButton {
    private static final Identifier SWITCH_SPRITE = ReliableEmi.res("textures/gui/switch.png");

    private boolean isChecked;
    private final boolean isEnabled = true;
    private OnCheckedChangeListener onCheckedChangeListener = (sw, checked) -> {};

    private Switch(int x, int y, Component message, boolean isChecked) {
        super(x, y, 0, 0, message);
        this.isChecked = isChecked;
        this.width = 29;
        this.height = 17;
    }

    @Override
    public void onPress(InputWithModifiers var1) {
        if (!isEnabled) return;
        isChecked = !isChecked;
        onCheckedChangeListener.onCheckedChanged(this, isChecked);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, createNarrationMessage());
        if (active) {
            var component = Component.translatable(isFocused() ? "narration.switch.usage.focused" : "narration.switch.usage.hovered");
            output.add(NarratedElementType.USAGE, component);
        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
//        RenderSystem.enableDepthTest();
//        guiGraphics.setColor(1F, 1F, 1F, alpha);
//        RenderSystem.enableBlend();
        int u = isChecked ? width : 0;
        int v = !isEnabled ? height * 3 : isHovered ? height : isFocused() ? height * 2 : 0;
        guiGraphics.blit(SWITCH_SPRITE, getX() - 1, getY() - 1, u, v, width, height, width * 2, height * 4);
    }

    @FunctionalInterface
    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch sw, boolean isChecked);
    }

    public static class Builder {
        private final Component message;
        private boolean isChecked = false;
        private OnCheckedChangeListener onCheckedChangeListener = (sw, checked) -> {};

        public Builder(Component message) { this.message = message; }

        public Builder setChecked(boolean checked) { this.isChecked = checked; return this; }

        public Builder onCheckedChangeListener(OnCheckedChangeListener listener) {
            this.onCheckedChangeListener = listener;
            return this;
        }

        public Switch build() {
            int x = 0;
            int y = 0;
            Switch sw = new Switch(x, y, message, isChecked);
            sw.onCheckedChangeListener = this.onCheckedChangeListener;
            return sw;
        }
    }
}
