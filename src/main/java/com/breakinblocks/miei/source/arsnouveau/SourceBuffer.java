package com.breakinblocks.miei.source.arsnouveau;

import aztech.modern_industrialization.machines.MachineComponent;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public final class SourceBuffer implements ISourceCap, MachineComponent.ServerOnly {
    private final int capacity;
    private final Runnable onChange;
    private int stored;

    public SourceBuffer(int capacity, Runnable onChange) {
        this.capacity = capacity;
        this.onChange = onChange;
    }

    public int stored() {
        return stored;
    }

    public int capacity() {
        return capacity;
    }

    public int room() {
        return capacity - stored;
    }

    public int take(int amount) {
        int taken = Math.min(amount, stored);
        if (taken > 0) {
            set(stored - taken);
        }
        return taken;
    }

    private void set(int value) {
        stored = Math.max(0, Math.min(capacity, value));
        onChange.run();
    }

    @Override
    public boolean canAcceptSource(int amount) {
        return amount > 0 && room() > 0;
    }

    @Override
    public boolean canProvideSource(int amount) {
        return false;
    }

    @Override
    public int getMaxExtract() {
        return 0;
    }

    @Override
    public int getMaxReceive() {
        return capacity;
    }

    @Override
    public int getSource() {
        return stored;
    }

    @Override
    public int getSourceCapacity() {
        return capacity;
    }

    @Override
    public void setSource(int source) {
        set(source);
    }

    @Override
    public void setMaxSource(int max) {}

    @Override
    public int receiveSource(int amount, boolean simulate) {
        int accepted = Math.max(0, Math.min(amount, room()));
        if (accepted > 0 && !simulate) {
            set(stored + accepted);
        }
        return accepted;
    }

    @Override
    public int extractSource(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public void writeNbt(CompoundTag tag, Provider registries) {
        tag.putInt("source", stored);
    }

    @Override
    public void readNbt(CompoundTag tag, Provider registries, boolean upgrading) {
        stored = Math.max(0, Math.min(capacity, tag.getInt("source")));
    }
}
