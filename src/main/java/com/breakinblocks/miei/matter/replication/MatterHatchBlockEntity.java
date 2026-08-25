package com.breakinblocks.miei.matter.replication;

import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import com.buuz135.replication.api.matter_fluid.MatterStack;

import net.minecraft.nbt.CompoundTag;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

public final class MatterHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final boolean input;
    private final MatterTank tank;

    public MatterHatchBlockEntity(BEP bep, String id, boolean input, double capacity) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
        this.input = input;
        this.tank = new MatterTank(input, capacity, this::setChanged);
        registerComponents(tank);
    }

    public boolean isInput() {
        return input;
    }

    public MatterTank tank() {
        return tank;
    }

    @Override
    public HatchType getHatchType() {
        return input ? MatterHatches.INPUT : MatterHatches.OUTPUT;
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
        MatterStack stored = tank.stored();
        if (stored.isEmpty()) {
            tag.putBoolean(HatchKeys.MATTER_EMPTY, true);
            return;
        }
        tag.putString(HatchKeys.MATTER_TYPE, "miei.matter." + stored.getMatterType().getName().toLowerCase());
        tag.putDouble(HatchKeys.MATTER_AMOUNT, stored.getAmount());
        tag.putDouble(HatchKeys.MATTER_CAPACITY, tank.capacity());
    }
}
