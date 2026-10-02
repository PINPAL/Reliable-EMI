package com.evandev.remi.feature.stackgroup.data;

import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Set;

public abstract class StackGroup {
    private final Identifier id;
    public final Component name;
    public boolean isEnabled = true;
    public int priority = 0;

    protected StackGroup(Identifier id, Component name) {
        this.id = id;
        this.name = name;
    }

    public Identifier getId() { return id; }

    public abstract boolean match(EmiIngredient stack);

    public Set<Identifier> getOptimizedIds() {
        return null;
    }
}