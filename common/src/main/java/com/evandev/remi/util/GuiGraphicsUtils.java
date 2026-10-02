package com.evandev.remi.util;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GuiGraphicsUtils {

    public static void renderItem(GuiGraphicsExtractor guiGraphics, ItemStack stack, float x, float y, float size) {
        if (stack == null || stack.isEmpty()) return;
//        Minecraft minecraft = Minecraft.getInstance();
//        var bakedModel = minecraft.getItemModelResolver().getItemModel(stack, minecraft.level, null, 0);
        guiGraphics.pose().pushMatrix();
//        guiGraphics.pose().translate(x + size / 2F, y + size / 2F);
        try {
//            guiGraphics.pose().scale(size, -size);
//            boolean flat = !bakedModel.usesBlockLight();
//            if (flat) minecraft.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
			// FIXME: idk what this shit is
//            minecraft.gameRenderer.itemInHandRenderer.renderItem(
//                    stack, ItemDisplayContext.GUI, false, guiGraphics.pose(),
//                    guiGraphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedModel
//            );
			guiGraphics.item(stack, (int) x, (int) y);
//            guiGraphics.flush();
//            if (flat) Lighting.setupFor3DItems();
        } catch (Throwable t) {
            var report = CrashReport.forThrowable(t, "Rendering item");
            report.addCategory("Item being rendered")
                    .setDetail("Item Type", stack.getItem()::toString)
                    .setDetail("Item Components", stack.getComponents()::toString)
                    .setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
            throw new ReportedException(report);
        }
        guiGraphics.pose().popMatrix();
    }
}
