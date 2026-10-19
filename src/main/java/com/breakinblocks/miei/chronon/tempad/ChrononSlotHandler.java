package com.breakinblocks.miei.chronon.tempad;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class ChrononSlotHandler implements IItemHandler {
    private final IItemHandler delegate;

    public ChrononSlotHandler(IItemHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!ChrononItems.holdsChronons(stack)) {
            return stack;
        }
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!ChrononItems.isSpent(delegate.getStackInSlot(slot))) {
            return ItemStack.EMPTY;
        }
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return ChrononItems.holdsChronons(stack) && delegate.isItemValid(slot, stack);
    }
}
