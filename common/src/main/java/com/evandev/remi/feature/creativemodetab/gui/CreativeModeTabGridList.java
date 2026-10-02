package com.evandev.remi.feature.creativemodetab.gui;

import com.evandev.remi.feature.creativemodetab.CreativeModeTabManager;
import com.evandev.remi.gui.GridList;
import com.evandev.remi.gui.ListEntry;
import com.evandev.remi.gui.components.Switch;
import com.evandev.remi.integration.emi.ScreenManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CreativeModeTabGridList extends GridList<Identifier> {
    private final Set<Identifier> disabledCreativeModeTabs;

    public CreativeModeTabGridList(CreativeModeTabConfigScreen screen, Set<Identifier> disabledCreativeModeTabs) {
        super(screen);
        this.disabledCreativeModeTabs = disabledCreativeModeTabs;
    }

    @Override
    public Collection<Identifier> getContents() {
        List<Identifier> result = new ArrayList<>();
        for (Identifier key : BuiltInRegistries.CREATIVE_MODE_TAB.keySet()) {
            Optional<Holder.Reference<CreativeModeTab>> tabOptionalRef = BuiltInRegistries.CREATIVE_MODE_TAB.get(key);
            if (tabOptionalRef.isEmpty()) continue;
			CreativeModeTab tab = tabOptionalRef.get().value();
            boolean notEmpty = !tab.getDisplayItems().isEmpty() || Minecraft.getInstance().level == null;
            if (notEmpty && !CreativeModeTabManager.HIDDEN_CREATIVE_MODE_TABS.contains(tab)) {
                result.add(key);
            }
        }
        return result;
    }

    @Override
    public ListEntry getEntryForContent(Identifier content, TripleEntry<Identifier> triple) {
	    return new StackGroupEntry(
	      content, triple, disabledCreativeModeTabs,
	      Optional.ofNullable(content)
	              .flatMap(BuiltInRegistries.CREATIVE_MODE_TAB::get)
	              .map(Holder.Reference::value)
	              .orElse(null)
	    );
    }

    public static class StackGroupEntry extends ListEntry {
        private final Identifier id;
        private final CreativeModeTab tab;
        private final Switch switchWidget;
        private final List<AbstractWidget> childWidgets;

        public StackGroupEntry(Identifier id, TripleEntry<Identifier> triple,
                               Set<Identifier> disabledTabs, CreativeModeTab tab) {
            super(triple);
            this.id = id;
            this.tab = tab;
            boolean checked = id != null && !disabledTabs.contains(id);
            this.switchWidget = new Switch.Builder(Component.empty())
                    .setChecked(checked)
                    .onCheckedChangeListener((sw, isChecked) -> {
                        if (id != null) {
                            if (isChecked) disabledTabs.remove(id);
                            else disabledTabs.add(id);
                        }
                    })
                    .build();
            this.childWidgets = new ArrayList<>(List.of(switchWidget));
        }

        @Override
        protected Switch getSwitch() {
            return switchWidget;
        }

        @Override
        protected List<AbstractWidget> getChildren() {
            return childWidgets;
        }

        @Override
        public boolean shouldRenderSwitch() {
            return tab != null && id != null;
        }

        @Override
        public Component getEntryTitle() {
            return tab != null ? tab.getDisplayName() : null;
        }

        @Override
        public void renderEntry(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int startX, int startY, float partialTick) {
            if (tab == null) return;
            var font = Minecraft.getInstance().font;
            int itemX = startX;
            int itemY = startY + ScreenManager.ENTRY_SIZE;
            int count = 0;
            for (var item : tab.getDisplayItems()) {
                if (item.isEmpty()) continue;
                if (count >= 8) break;
                guiGraphics.item(item, itemX, itemY);
                guiGraphics.itemDecorations(font, item, itemX, itemY, "");
                itemX += ScreenManager.ENTRY_SIZE;
                count++;
            }
        }

	    @Override
	    protected int contentHeight() {
		    return childWidgets.stream().mapToInt(AbstractWidget::getHeight).sum();
	    }
    }
}
