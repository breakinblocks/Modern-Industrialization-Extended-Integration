package com.breakinblocks.miei.heat.pneumaticcraft;

import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import net.minecraft.nbt.CompoundTag;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

public final class HeatHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final HeatExchanger exchanger;

    public HeatHatchBlockEntity(BEP bep, String id, double thermalCapacity) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
        this.exchanger = new HeatExchanger(thermalCapacity);
        registerComponents(exchanger);
    }

    public HeatExchanger exchanger() {
        return exchanger;
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && !level.isClientSide) {
            exchanger.tick(level, worldPosition);
        }
    }

    @Override
    public HatchType getHatchType() {
        return HeatHatches.TYPE;
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
        tag.putDouble(HatchKeys.TEMPERATURE, exchanger.temperature());
    }
}
