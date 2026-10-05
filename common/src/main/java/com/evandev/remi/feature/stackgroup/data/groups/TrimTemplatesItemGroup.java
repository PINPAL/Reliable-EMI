package com.evandev.remi.feature.stackgroup.data.groups;

import com.evandev.remi.feature.stackgroup.data.StackGroup;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.SmithingTemplateItem;

public class TrimTemplatesItemGroup extends StackGroup {
    public TrimTemplatesItemGroup() {
        super(Identifier.withDefaultNamespace("trim_templates"), null);
    }

    @Override
    public boolean match(EmiIngredient stack) {
	    if (!(stack instanceof EmiStack s)) return false;

	    return s.getItemStack().getItem() instanceof SmithingTemplateItem t
	           // Check if the template accepts more than 1 additional slot (to isolate armor trims from upgrade templates)
	           && t.getAdditionalSlotEmptyIcons().size() > 1;
    }

}
