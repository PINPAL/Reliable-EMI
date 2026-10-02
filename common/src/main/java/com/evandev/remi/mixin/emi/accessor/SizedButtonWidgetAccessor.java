package com.evandev.remi.mixin.emi.accessor;

import dev.emi.emi.screen.widget.SizedButtonWidget;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SizedButtonWidget.class, remap = false)
public interface SizedButtonWidgetAccessor {
    @Accessor(value = "texture", remap = false)
    void remi$setTexture(Identifier texture);
}
