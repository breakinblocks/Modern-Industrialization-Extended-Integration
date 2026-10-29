package com.breakinblocks.miei.compat;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public final class ConditionIcons {
    private ConditionIcons() {}

    public static ItemStack hatch(List<ItemStack> stacks, boolean output) {
        if (stacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return (output && stacks.size() > 1 ? stacks.get(1) : stacks.get(0)).copy();
    }
}
