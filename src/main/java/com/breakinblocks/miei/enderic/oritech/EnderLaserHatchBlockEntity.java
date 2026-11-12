package com.breakinblocks.miei.enderic.oritech;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.nbt.CompoundTag;

public final class EnderLaserHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final Receiver receiver = new Receiver();
    private long tick = Long.MIN_VALUE;
    private long received;
    private long lastReceived;

    public EnderLaserHatchBlockEntity(BEP bep, String id) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
    }

    public ILongEnergyStorage receiver() {
        return receiver;
    }

    private void accept(long amount) {
        if (level == null || amount <= 0) {
            return;
        }
        roll(level.getGameTime());
        long sum = received + amount;
        received = sum < 0 ? Long.MAX_VALUE : sum;
    }

    private void roll(long now) {
        if (tick == now) {
            return;
        }
        boolean consecutive = tick == now - 1;
        lastReceived = consecutive ? received : 0;
        tick = now;
        received = 0;
    }

    public long rate() {
        if (level == null) {
            return 0;
        }
        long now = level.getGameTime();
        return tick == now ? lastReceived : tick == now - 1 ? received : 0;
    }

    @Override
    public HatchType getHatchType() {
        return EnderLaserHatches.TYPE;
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
        tag.putLong(HatchKeys.ENDER_LASER_RATE, rate());
    }

    private final class Receiver implements ILongEnergyStorage {
        @Override
        public long receive(long amount, boolean simulate) {
            if (amount <= 0) {
                return 0;
            }
            if (!simulate) {
                accept(amount);
            }
            return amount;
        }

        @Override
        public long extract(long amount, boolean simulate) {
            return 0;
        }

        @Override
        public long getAmount() {
            return 0;
        }

        @Override
        public long getCapacity() {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
