package com.breakinblocks.miei.mixin;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ConfigurableItemStack.ConfigurableItemSlot.class, remap = false)
public interface ConfigurableItemSlotAccessor {
    @Mutable
    @Accessor("insertPredicate")
    void miei$setInsertPredicate(Predicate<ItemStack> predicate);
}
