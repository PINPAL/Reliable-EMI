package com.evandev.remi.feature.creativemodetab.gui.itemtab;

import com.evandev.ReliableEmi;
import com.evandev.remi.config.ReliableEmiConfig;
import com.evandev.remi.integration.emi.ScreenManager;
import com.evandev.remi.util.GuiGraphicsUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.config.SidebarTheme;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class ItemTabButton extends TabButton {
    private static final TabSprites VERTICAL_SPRITES = new TabSprites(
            ReliableEmi.res("widget/tabs/vertical_modern"),
            ReliableEmi.res("widget/tabs/vertical_modern_first"),
            ReliableEmi.res("widget/tabs/vertical_modern_last"),
            ReliableEmi.res("widget/tabs/vertical_modern_selected"),
            ReliableEmi.res("widget/tabs/vertical_modern_first_selected"),
            ReliableEmi.res("widget/tabs/vertical_modern_last_selected")
    );
    private static final TabSprites HORIZONTAL_SPRITES = new TabSprites(
            ReliableEmi.res("widget/tabs/horizontal_modern"),
            ReliableEmi.res("widget/tabs/horizontal_modern_first"),
            ReliableEmi.res("widget/tabs/horizontal_modern_last"),
            ReliableEmi.res("widget/tabs/horizontal_modern_selected"),
            ReliableEmi.res("widget/tabs/horizontal_modern_first_selected"),
            ReliableEmi.res("widget/tabs/horizontal_modern_last_selected")
    );
    private static final TabSprites VERTICAL_VANILLA_SPRITES = new TabSprites(
            ReliableEmi.res("widget/tabs/vertical_vanilla"),
            ReliableEmi.res("widget/tabs/vertical_vanilla_first"),
            ReliableEmi.res("widget/tabs/vertical_vanilla_last"),
            ReliableEmi.res("widget/tabs/vertical_vanilla_selected"),
            ReliableEmi.res("widget/tabs/vertical_vanilla_first_selected"),
            ReliableEmi.res("widget/tabs/vertical_vanilla_last_selected")
    );
    private static final TabSprites HORIZONTAL_VANILLA_SPRITES = new TabSprites(
            ReliableEmi.res("widget/tabs/horizontal_vanilla"),
            ReliableEmi.res("widget/tabs/horizontal_vanilla_first"),
            ReliableEmi.res("widget/tabs/horizontal_vanilla_last"),
            ReliableEmi.res("widget/tabs/horizontal_vanilla_selected"),
            ReliableEmi.res("widget/tabs/horizontal_vanilla_first_selected"),
            ReliableEmi.res("widget/tabs/horizontal_vanilla_last_selected")
    );

    private static Method recreativeIconMethod = null;
    private static boolean checkedRecreativeMethod = false;

    private final ItemTabManager tabManager;
    private final ItemTab tab;
    private final ButtonStyle style;
    private final Component title;
    private final TabPosition position;
    private Identifier customIcon;
    private Component lastDisplayTitle;

    public ItemTabButton(ItemTabManager tabManager, ItemTab tab, int width, int height,
                         ButtonStyle style, TabPosition tabPosition) {
        super(tabManager, tab, width, height);
        this.tabManager = tabManager;
        this.tab = tab;
        this.style = style;
        this.position = tabPosition;
        this.title = tab.creativeModeTab() != null ? tab.creativeModeTab().getDisplayName() : null;
        this.customIcon = fetchRecreativeIcon(tab.creativeModeTab());
        this.visible = tab.creativeModeTab() != null;
    }

    private static Identifier fetchRecreativeIcon(CreativeModeTab tab) {
        if (tab == null) return null;
        if (!checkedRecreativeMethod) {
            try {
                recreativeIconMethod = CreativeModeTab.class.getMethod("recreative$getCustomIcon");
            } catch (Exception ignored) {
            }
            checkedRecreativeMethod = true;
        }
        if (recreativeIconMethod != null) {
            try {
                return (Identifier) recreativeIconMethod.invoke(tab);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private Identifier getCustomIcon() {
        if (this.customIcon == null && this.tab.creativeModeTab() != null) {
            this.customIcon = fetchRecreativeIcon(this.tab.creativeModeTab());
        }
        return this.customIcon;
    }

    private boolean isVisible() {
        return tab.creativeModeTab() != null;
    }

    @Override
    public void onClick(final MouseButtonEvent event, final boolean doubleClickY) {
        if (!isVisible()) return;
        super.onClick(event, doubleClickY);
        tabManager.onTabSelected(tab);
    }

    @Override
    public void extractWidgetRenderState(@NotNull GuiGraphicsExtractor raw, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;

//        RenderSystem.enableBlend();
//        RenderSystem.enableDepthTest();
        EmiDrawContext context = EmiDrawContext.wrap(raw);
        EmiScreenManager.SidebarPanel panel = ScreenManager.getTargetCreativeTabPanel();
        boolean isVanillaTheme = panel != null && panel.theme == SidebarTheme.VANILLA;

        Identifier icon = getCustomIcon();

        int iconSize = ReliableEmiConfig.tabIconSize;
        int iconX = getX() + (getWidth() - iconSize) / 2;
        int iconY = getY() + (getHeight() - iconSize) / 2;

        raw.pose().pushMatrix();
        raw.pose().translate(0.0f, 0.0f);

        TabSprites sprites = isVanillaTheme ? HORIZONTAL_VANILLA_SPRITES : HORIZONTAL_SPRITES;

        if (style == ButtonStyle.TOP) {
            iconY -= (panel != null ? panel.theme.verticalPadding : 0) / 4;
        } else {
            iconX -= (panel != null ? panel.theme.verticalPadding : 0) / 4;
            sprites = isVanillaTheme ? VERTICAL_VANILLA_SPRITES : VERTICAL_SPRITES;
        }

        raw.blitSprite(RenderPipelines.GUI_TEXTURED, sprites.get(isSelected(), position), getX(), getY(), getWidth(), getHeight());
        raw.pose().popMatrix();

        if (icon != null) {
            raw.pose().pushMatrix();
            raw.pose().translate(iconX, iconY);
            raw.pose().scale(iconSize / 16f, iconSize / 16f);
            raw.blit(icon, 0, 0, 0, 0, 16, 16, 16, 16);
            raw.pose().popMatrix();
        } else if (tab.creativeModeTab() != null) {
            GuiGraphicsUtils.renderItem(raw, tab.creativeModeTab().getIconItem(), iconX, iconY, iconSize);
        }

        if (isHovered && title != null) {
            if (ReliableEmiConfig.showCreativeTabNameInSearchbar && !ReliableEmiConfig.showTitleInsteadOfPageNumbers) {
                ScreenManager.setCustomIndexTitle(title);
                lastDisplayTitle = ScreenManager.customIndexTitle;
            }

	        raw.setTooltipForNextFrame(title, mouseX, mouseY);
        } else if (!ReliableEmiConfig.showTitleInsteadOfPageNumbers) {
            ScreenManager.removeCustomIndexTitle(lastDisplayTitle != null ? lastDisplayTitle : title);
        }

//        RenderSystem.disableBlend();
    }

    public enum ButtonStyle {TOP, LEFT, RIGHT}

    public enum TabPosition {FIRST, MIDDLE, LAST}

    public record TabSprites(Identifier middle, Identifier first, Identifier last,
                             Identifier middleSelected, Identifier firstSelected,
                             Identifier lastSelected) {
        public Identifier get(boolean selected, TabPosition position) {
            if (selected) {
                return switch (position) {
                    case FIRST -> firstSelected;
                    case MIDDLE -> middleSelected;
                    case LAST -> lastSelected;
                };
            } else {
                return switch (position) {
                    case FIRST -> first;
                    case MIDDLE -> middle;
                    case LAST -> last;
                };
            }
        }
    }
}