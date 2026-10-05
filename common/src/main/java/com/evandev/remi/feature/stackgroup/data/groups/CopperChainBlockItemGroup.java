package com.evandev.remi.feature.stackgroup.data.groups;

import com.evandev.remi.feature.stackgroup.data.StackGroup;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;

import java.util.Set;
import java.util.stream.Collectors;

public class CopperChainBlockItemGroup extends StackGroup {
	private static final Set<Identifier> COPPER_BLOCKS =
	  BlockItemIds.COPPER_BARS.asList().stream().map(b -> b.item().identifier())
	                           .collect(Collectors.toUnmodifiableSet());

    public CopperChainBlockItemGroup() {
        super(Identifier.withDefaultNamespace("copper_chains"), null);
    }

	@Override
	public boolean match(EmiIngredient stack) {
		return stack instanceof EmiStack s && COPPER_BLOCKS.contains(s.getId());
	}
}
