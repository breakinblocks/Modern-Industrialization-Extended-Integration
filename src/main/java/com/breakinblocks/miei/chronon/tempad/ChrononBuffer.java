package com.breakinblocks.miei.chronon.tempad;

import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;

import aztech.modern_industrialization.machines.MachineComponent;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public final class ChrononBuffer implements ChrononHandler, MachineComponent.ServerOnly {
    private final int capacity;
    private final Runnable onChange;
    private int stored;

    public ChrononBuffer(int capacity, Runnable onChange) {
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

    public int add(int amount) {
        int accepted = Math.min(amount, room());
        if (accepted > 0) {
            set(stored + accepted);
        }
        return accepted;
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
    public int getPower() {
        return stored;
    }

    @Override
    public int getMaxPower() {
        return capacity;
    }

    @Override
    public boolean getCanExtract() {
        return false;
    }

    @Override
    public int extract(int amount, ActionType action) {
        return 0;
    }

    @Override
    public int insert(int amount, ActionType action) {
        int accepted = Math.max(0, Math.min(amount, room()));
        if (accepted > 0 && action == ActionType.Execute) {
            set(stored + accepted);
        }
        return accepted;
    }

    @Override
    public void writeNbt(CompoundTag tag, Provider registries) {
        tag.putInt("chronons", stored);
    }

    @Override
    public void readNbt(CompoundTag tag, Provider registries, boolean upgrading) {
        stored = Math.max(0, Math.min(capacity, tag.getInt("chronons")));
    }
}
