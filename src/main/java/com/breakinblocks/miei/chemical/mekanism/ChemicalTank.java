package com.breakinblocks.miei.chemical.mekanism;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;

import aztech.modern_industrialization.machines.MachineComponent;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public final class ChemicalTank implements IChemicalHandler, MachineComponent.ServerOnly {
    private final boolean input;
    private final IChemicalTank tank;

    public ChemicalTank(boolean input, long capacity, Runnable onChange) {
        this.input = input;
        this.tank = input
            ? BasicChemicalTank.inputModern(capacity, stack -> true, onChange::run)
            : BasicChemicalTank.output(capacity, onChange::run);
    }

    public boolean isInput() {
        return input;
    }

    public long amountOf(Holder<Chemical> chemical) {
        ChemicalStack stored = tank.getStack();
        return stored.isEmpty() || !stored.is(chemical.value()) ? 0 : stored.getAmount();
    }

    public long spaceFor(Holder<Chemical> chemical) {
        ChemicalStack stored = tank.getStack();
        if (stored.isEmpty()) {
            return tank.getCapacity();
        }
        return stored.is(chemical.value()) ? tank.getCapacity() - stored.getAmount() : 0;
    }

    public long take(Holder<Chemical> chemical, long amount) {
        if (amountOf(chemical) <= 0) {
            return 0;
        }
        return tank.extract(amount, Action.EXECUTE, AutomationType.INTERNAL).getAmount();
    }

    public long add(Holder<Chemical> chemical, long amount) {
        long accepted = Math.min(amount, spaceFor(chemical));
        if (accepted <= 0) {
            return 0;
        }
        ChemicalStack leftover = tank.insert(new ChemicalStack(chemical, accepted), Action.EXECUTE, AutomationType.INTERNAL);
        return accepted - leftover.getAmount();
    }

    @Override
    public int getChemicalTanks() {
        return 1;
    }

    @Override
    public ChemicalStack getChemicalInTank(int index) {
        return tank.getStack();
    }

    @Override
    public void setChemicalInTank(int index, ChemicalStack stack) {
        tank.setStack(stack);
    }

    @Override
    public long getChemicalTankCapacity(int index) {
        return tank.getCapacity();
    }

    @Override
    public boolean isValid(int index, ChemicalStack stack) {
        return tank.isValid(stack);
    }

    @Override
    public ChemicalStack insertChemical(int index, ChemicalStack stack, Action action) {
        return input ? tank.insert(stack, action, AutomationType.EXTERNAL) : stack;
    }

    @Override
    public ChemicalStack extractChemical(int index, long amount, Action action) {
        return input ? ChemicalStack.EMPTY : tank.extract(amount, action, AutomationType.EXTERNAL);
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tank.isEmpty()) {
            tag.put("chemical", tank.serializeNBT(registries));
        }
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean upgrading) {
        if (tag.contains("chemical")) {
            tank.deserializeNBT(registries, tag.getCompound("chemical"));
        } else {
            tank.setEmpty();
        }
    }
}
