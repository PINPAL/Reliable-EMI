package com.evandev.remi.gui.components;

import com.evandev.ReliableEmi;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ImageButton extends SizedButtonWidget {
    private static final Identifier DEFAULT_TEXTURE = ReliableEmi.res("textures/gui/buttons.png");

    private final boolean matchScreenManagerVisibility;
    private final int textureWidth;
    private final int textureHeight;

	public ImageButton(int width, int height, int u, int v, BooleanSupplier isActive, OnPress action, List<Component> text) {
		this(width, height, u, v, isActive, action, () -> text, false, DEFAULT_TEXTURE, 256, 256);
	}

	public ImageButton(
	  int width, int height, int u, int v, BooleanSupplier isActive, OnPress action,
	  boolean matchScreenManagerVisibility, Identifier customTexture, int textureWidth, int textureHeight
	) {
		this(width, height, u, v, isActive, action, null, matchScreenManagerVisibility, customTexture, textureWidth, textureHeight);
	}

	public ImageButton(
	  int width, int height, int u, int v, BooleanSupplier isActive, OnPress action, Supplier<List<Component>> text,
	  boolean matchScreenManagerVisibility, Identifier customTexture, int textureWidth, int textureHeight
	) {
		super(0, 0, width, height, u, v, isActive, action, () -> 0, text);
		this.matchScreenManagerVisibility = matchScreenManagerVisibility;
		this.texture = customTexture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

    @Override
    public void extractContents(GuiGraphicsExtractor raw, int mouseX, int mouseY, float delta) {
        if (matchScreenManagerVisibility && EmiScreenManager.isDisabled()) return;

	    EmiDrawContext context = EmiDrawContext.wrap(raw);
	    context.enableDepthTest();
        context.drawTexture(texture, getX(), getY(), getU(mouseX, mouseY), getV(mouseX, mouseY), width, height, textureWidth, textureHeight);

        if (isMouseOver(mouseX, mouseY) && text != null && active) {
            context.push();
	        context.disableDepthTest();
            List<ClientTooltipComponent> texts = text.get().stream()
                    .map(EmiPort::ordered).map(ClientTooltipComponent::create).toList();
	        //noinspection DataFlowIssue
	        EmiRenderHelper.drawTooltip(Minecraft.getInstance().gui.screen(), context, texts, mouseX, mouseY);
            context.pop();
        }
    }
}
