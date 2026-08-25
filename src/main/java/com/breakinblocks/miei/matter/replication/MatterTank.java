package com.breakinblocks.miei.matter.replication;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.matter_fluid.IMatterHandler;
import com.buuz135.replication.api.matter_fluid.MatterStack;

import aztech.modern_industrialization.machines.MachineComponent;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class MatterTank implements IMatterHandler, MachineComponent.ServerOnly {
    private final boolean input;
    private final double capacity;
    private final Runnable onChange;
    private MatterStack stored = MatterStack.EMPTY;

    public MatterTank(boolean input, double capacity, Runnable onChange) {
        this.input = input;
        this.capacity = capacity;
        this.onChange = onChange;
    }

    public boolean isInput() {
        return input;
    }

    public MatterStack stored() {
        return stored;
    }

    public double capacity() {
        return capacity;
    }

    public double amountOf(IMatterType type) {
        return stored.isEmpty() || stored.getMatterType() != type ? 0 : stored.getAmount();
    }

    public double spaceFor(IMatterType type) {
        if (stored.isEmpty()) {
            return capacity;
        }
        return stored.getMatterType() == type ? capacity - stored.getAmount() : 0;
    }

    public double take(IMatterType type, double amount, boolean simulate) {
        double taken = Math.min(amount, amountOf(type));
        if (taken > 0 && !simulate) {
            set(stored.getAmount() - taken <= 0 ? MatterStack.EMPTY : new MatterStack(stored, stored.getAmount() - taken));
        }
        return taken;
    }

    public double add(IMatterType type, double amount, boolean simulate) {
        double accepted = Math.min(amount, spaceFor(type));
        if (accepted > 0 && !simulate) {
            set(new MatterStack(type, (stored.isEmpty() ? 0 : stored.getAmount()) + accepted));
        }
        return accepted;
    }

    private void set(MatterStack stack) {
        stored = stack == null || stack.isEmpty() || stack.getAmount() <= 0 ? MatterStack.EMPTY : stack;
        onChange.run();
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public MatterStack getMatterInTank(int tank) {
        return stored;
    }

    @Override
    public double getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isMatterValid(int tank, MatterStack stack) {
        return stack != null && !stack.isEmpty();
    }

    @Override
    public double fill(MatterStack resource, IFluidHandler.FluidAction action) {
        if (!input || resource == null || resource.isEmpty()) {
            return 0;
        }
        return add(resource.getMatterType(), resource.getAmount(), action.simulate());
    }

    @Override
    public MatterStack drain(MatterStack resource, IFluidHandler.FluidAction action) {
        if (input || resource == null || resource.isEmpty() || stored.isEmpty() || stored.getMatterType() != resource.getMatterType()) {
            return MatterStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public MatterStack drain(double amount, IFluidHandler.FluidAction action) {
        if (input || stored.isEmpty()) {
            return MatterStack.EMPTY;
        }
        IMatterType type = stored.getMatterType();
        double taken = take(type, amount, action.simulate());
        return taken <= 0 ? MatterStack.EMPTY : new MatterStack(type, taken);
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        if (!stored.isEmpty()) {
            tag.put("matter", stored.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean upgrading) {
        stored = tag.contains("matter") ? MatterStack.loadMatterStackFromNBT(tag.getCompound("matter")) : MatterStack.EMPTY;
    }
}
