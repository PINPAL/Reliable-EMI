package com.evandev.remi.feature.stackgroup.data.groups;

import com.evandev.remi.feature.stackgroup.data.StackGroup;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;

public class AnimalArmorItemGroup extends StackGroup {
	private static final Holder<EntityType<?>> PLAYER = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityTypes.PLAYER);

    public AnimalArmorItemGroup() {
        super(Identifier.withDefaultNamespace("animal_armors"), null);
    }

    @Override
    public boolean match(EmiIngredient stack) {
        if (!(stack instanceof EmiStack s)) return false;

	    Equippable equippable = s.getItemStack().get(DataComponents.EQUIPPABLE);
		if (equippable == null || equippable.canBeEquippedBy(PLAYER)) return false;

	    ItemAttributeModifiers modifiers = s.getItemStack().get(DataComponents.ATTRIBUTE_MODIFIERS);
	    return modifiers != null && modifiers.modifiers().stream().anyMatch(
	      e -> e.attribute().is(Attributes.ARMOR) && e.slot().test(equippable.slot()));
    }

}
