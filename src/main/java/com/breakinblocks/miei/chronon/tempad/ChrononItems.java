package com.breakinblocks.miei.chronon.tempad;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.world.item.ItemStack;

public final class ChrononItems {
    private ChrononItems() {}

    public static ChrononHandler handler(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return ChrononHandler.Capabilities.getItem().getCapability(stack, null);
    }

    public static boolean holdsChronons(ItemStack stack) {
        return handler(stack) != null;
    }

    public static boolean isSpent(ItemStack stack) {
        ChrononHandler handler = handler(stack);
        return handler == null || handler.getPower() <= 0 || !handler.getCanExtract();
    }

    public static ItemStack stackOf(ConfigurableItemStack slot) {
        if (slot.isEmpty() || slot.getAmount() != 1) {
            return ItemStack.EMPTY;
        }
        return slot.getVariant().toStack(1);
    }

    public static int draw(ConfigurableItemStack slot, ChrononBuffer buffer, int limit) {
        int room = Math.min(buffer.room(), limit);
        if (room <= 0) {
            return 0;
        }
        ItemStack stack = stackOf(slot);
        ChrononHandler handler = handler(stack);
        if (handler == null || !handler.getCanExtract()) {
            return 0;
        }
        int taken = handler.extract(room, ActionType.Execute);
        if (taken <= 0) {
            return 0;
        }
        slot.setKey(ItemVariant.of(stack));
        return buffer.add(taken);
    }
}
