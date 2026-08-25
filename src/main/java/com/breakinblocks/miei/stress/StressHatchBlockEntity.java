package com.breakinblocks.miei.stress;

import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import net.minecraft.nbt.CompoundTag;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

public final class StressHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private static final long TIMEOUT = 3;

    private double demand;
    private long demandTick = Long.MIN_VALUE;
    private float speed;
    private double spare;
    private long networkId;
    private long linkTick = Long.MIN_VALUE;

    public StressHatchBlockEntity(BEP bep, String id) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
    }

    public void setDemand(double su) {
        demand = su;
        demandTick = now();
    }

    public double demand() {
        return fresh(demandTick) ? demand : 0;
    }

    public void link(float speed, double spare, long networkId) {
        this.speed = speed;
        this.spare = spare;
        this.networkId = networkId;
        this.linkTick = now();
    }

    public boolean isLinked() {
        return fresh(linkTick);
    }

    public float speed() {
        return isLinked() ? speed : 0;
    }

    public double spare() {
        return isLinked() ? spare : 0;
    }

    public long networkId() {
        return networkId;
    }

    private long now() {
        return level == null ? Long.MIN_VALUE : level.getGameTime();
    }

    private boolean fresh(long tick) {
        return level != null && level.getGameTime() - tick <= TIMEOUT;
    }

    @Override
    public HatchType getHatchType() {
        return StressHatches.TYPE;
    }

    @Override
    public boolean upgradesToSteel() {
        return false;
    }

    @Override
    public MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public void addJadeData(CompoundTag tag) {
        boolean linked = isLinked();
        tag.putBoolean(HatchKeys.STRESS_LINKED, linked);
        if (linked) {
            tag.putFloat(HatchKeys.STRESS_SPEED, speed());
            tag.putDouble(HatchKeys.STRESS_DEMAND, demand());
        }
    }
}
