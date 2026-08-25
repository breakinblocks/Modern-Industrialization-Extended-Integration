package com.breakinblocks.miei.mixin;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MultiblockMachineBlockEntity.class, remap = false)
public interface MultiblockMachineBlockEntityAccessor {
    @Accessor("shapeMatcher")
    ShapeMatcher miei$getShapeMatcher();
}
