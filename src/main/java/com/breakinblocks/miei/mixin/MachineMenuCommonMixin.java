package com.breakinblocks.miei.mixin;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.ComponentStorage;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.gui.MachineMenuCommon;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chronon.tempad.ChrononHatches;
import com.breakinblocks.miei.chronon.tempad.ChrononItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MachineMenuCommon.class, remap = false)
public abstract class MachineMenuCommonMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void miei$filterChrononSlot(
            int syncId,
            Inventory playerInventory,
            MIInventory inventory,
            MachineGuiParameters guiParams,
            ComponentStorage<?> components,
            CallbackInfo ci) {
        if (!MIEI.TEMPAD || !MI.id(ChrononHatches.ID).equals(guiParams.blockId)) {
            return;
        }
        for (Slot slot : ((AbstractContainerMenu) (Object) this).slots) {
            if (slot instanceof ConfigurableItemStack.ConfigurableItemSlot itemSlot) {
                ((ConfigurableItemSlotAccessor) itemSlot).miei$setInsertPredicate(ChrononItems::holdsChronons);
            }
        }
    }
}
