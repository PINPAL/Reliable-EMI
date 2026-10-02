package com.evandev.remi.mixin.minecraft;

import com.evandev.ReliableEmi;
import com.evandev.remi.config.ReliableEmiConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.emi.emi.screen.widget.EmiSearchWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @Unique
    private static final WidgetSprites remi$SPRITES = new WidgetSprites(
            ReliableEmi.res("widget/text_field"),
            ReliableEmi.res("widget/text_field_highlighted")
    );

    @WrapOperation(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void drawSearchWidgetBackground(
      GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int x, int y, int width,
      int height, Operation<Void> original
    ) {
        EditBox editBox = (EditBox) (Object) this;
        if (editBox instanceof EmiSearchWidget && !ReliableEmiConfig.searchWidgetUseVanillaTexture) {
            int horizontalPadding = ReliableEmiConfig.searchWidgetHorizontalPadding;
            int verticalPadding = ReliableEmiConfig.searchWidgetVerticalPadding;
            location = remi$SPRITES.get(editBox.isActive(), editBox.isFocused());
            x = x - horizontalPadding;
            y = y - verticalPadding;
            width = width + horizontalPadding * 2;
            height = height + verticalPadding * 2;
        }
        original.call(instance, renderPipeline, location, x, y, width, height);
    }

    @WrapOperation(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", ordinal = 0), require = 0)
    private void drawSuggestionString(
      GuiGraphicsExtractor instance, Font font, Component str, int x, int y, int color, Operation<Void> original) {
        EditBox editBox = (EditBox) (Object) this;
        if (editBox instanceof EmiSearchWidget) {
            color = ReliableEmiConfig.searchWidgetSuggestionTextColor;
        }
        original.call(instance, font, str, x, y, color);
    }

    @WrapOperation(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V", ordinal = 0), require = 0)
    private void drawSuggestionStringNeo(GuiGraphicsExtractor instance, Font font, String str, int x, int y, int color, boolean dropShadow, Operation<Integer> original) {
        EditBox editBox = (EditBox) (Object) this;
        if (editBox instanceof EmiSearchWidget) {
            color = ReliableEmiConfig.searchWidgetSuggestionTextColor;
        }
        original.call(instance, font, str, x, y, color, dropShadow);
    }

    @ModifyExpressionValue(
            method = "extractWidgetRenderState",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/EditBox;textColor:I", opcode = Opcodes.GETFIELD)
    )
    private int overrideTextColor(int original) {
        EditBox editBox = (EditBox) (Object) this;
        return (editBox instanceof EmiSearchWidget) ? ReliableEmiConfig.searchWidgetTextColor : original;
    }

    @WrapOperation(
            method = "extractWidgetRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;applyFormat(Ljava/lang/String;I)Lnet/minecraft/util/FormattedCharSequence;")
    )
    private FormattedCharSequence overrideFormattedTextColor(
      EditBox instance, String text, int offset, Operation<FormattedCharSequence> original
    ) {
        FormattedCharSequence sequence = original.call(instance, text, offset);
        EditBox editBox = (EditBox) (Object) this;
        if (editBox instanceof EmiSearchWidget) {
            int customColor = ReliableEmiConfig.searchWidgetTextColor;
            return (FormattedCharSequence) sink -> sequence.accept((index, style, codePoint) -> {
                if (style.getColor() != null && style.getColor().getValue() == 0xFFFFFF) {
                    style = style.withColor(customColor);
                }
                return sink.accept(index, style, codePoint);
            });
        }
        return sequence;
    }
}