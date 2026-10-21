package com.breakinblocks.miei.laser.nautec;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import com.breakinblocks.nautec.api.blockentities.LaserBlockEntity;
import com.breakinblocks.nautec.capabilities.beam.IBeamReceiver;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public final class LaserHatchBlockEntity extends HatchBlockEntity implements HatchInfo, IBeamReceiver {
    private long tick = Long.MIN_VALUE;
    private int power;
    private float purity;
    private int lastPower;
    private float lastPurity;

    public LaserHatchBlockEntity(BEP bep, String id) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
    }

    @Override
    public void receiveBeam(int power, float purity, Direction travel, BlockPos origin, LongSet trail) {
        if (level == null || power <= 0) {
            return;
        }
        roll(level.getGameTime());
        this.purity = LaserBlockEntity.mergedPurity(this.power, this.purity, power, purity);
        this.power = (int) Math.min(Integer.MAX_VALUE, (long) this.power + power);
    }

    private void roll(long now) {
        if (tick == now) {
            return;
        }
        boolean consecutive = tick == now - 1;
        lastPower = consecutive ? power : 0;
        lastPurity = consecutive ? purity : 0;
        tick = now;
        power = 0;
        purity = 0;
    }

    public int power() {
        if (level == null) {
            return 0;
        }
        long now = level.getGameTime();
        return tick == now ? lastPower : tick == now - 1 ? power : 0;
    }

    public float purity() {
        if (level == null) {
            return 0;
        }
        long now = level.getGameTime();
        return tick == now ? lastPurity : tick == now - 1 ? purity : 0;
    }

    @Override
    public HatchType getHatchType() {
        return LaserHatches.TYPE;
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
        tag.putInt(HatchKeys.LASER_POWER, power());
        tag.putFloat(HatchKeys.LASER_PURITY, purity());
    }
}
