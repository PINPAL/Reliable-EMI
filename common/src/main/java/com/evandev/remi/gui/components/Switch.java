package com.evandev.remi.gui.components;

import com.evandev.ReliableEmi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class Switch extends AbstractButton {
    private static final Identifier SWITCH_SPRITE = ReliableEmi.res("textures/gui/switch.png");
	private static final int TEXTURE_WIDTH = 58;
	private static final int TEXTURE_HEIGHT = 68;

    private boolean isChecked;
    private final boolean isEnabled = true;
    private OnCheckedChangeListener onCheckedChangeListener = (sw, checked) -> {};

    private Switch(int x, int y, Component message, boolean isChecked) {
        super(x, y, 29, 17, message);
        this.isChecked = isChecked;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers var1) {
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
        int u = isChecked ? width : 0;
        int v = !isEnabled ? height * 3 : isHovered() ? height : isFocused() ? height * 2 : 0;
		int xPos = getX() + (isChecked ? 1 : 0); // offset right by 1 if checked to avoid shifting position
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SWITCH_SPRITE, xPos, getY(), u, v, width, height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
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
