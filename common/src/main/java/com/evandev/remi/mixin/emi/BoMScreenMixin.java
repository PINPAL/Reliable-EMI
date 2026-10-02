package com.evandev.remi.mixin.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.BoMScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoMScreen.class, remap = false)
public class BoMScreenMixin extends Screen {

    protected BoMScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "extractRenderState", remap = true)
    private void render(GuiGraphicsExtractor raw, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        extractBlurredBackground(raw); // Render the vanilla blurry background
    }

    @WrapOperation(method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/runtime/EmiDrawContext;fill(IIIII)V", remap = false),
            remap = true)
    private void modifyMouseReleased(EmiDrawContext instance, int x, int y, int width, int height, int color,
                                     Operation<Void> original) {
        // Passed so that the dark background will not be rendered
    }

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE",
            target = "Ldev/emi/emi/screen/BoMScreen;extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"),
            remap = true)
    private void modifyMouseReleased(BoMScreen instance, GuiGraphicsExtractor guiGraphics, Operation<Void> original) {
        // Passed so that the dirt background will not be rendered
    }

}