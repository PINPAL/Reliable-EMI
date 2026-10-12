package com.evandev.remi.mixin.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.BoMScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BoMScreen.class, remap = false)
public class BoMScreenMixin {

	@WrapOperation(
	  method = "extractRenderState",
	  at = @At(value = "INVOKE", target = "Ldev/emi/emi/runtime/EmiDrawContext;fill(IIIII)V", remap = false),
	  remap = true
	)
    private void cancelBackground(EmiDrawContext instance, int x, int y, int width, int height, int color,
                                     Operation<Void> original) {
        // Passed so that the dark background will not be rendered
    }

}