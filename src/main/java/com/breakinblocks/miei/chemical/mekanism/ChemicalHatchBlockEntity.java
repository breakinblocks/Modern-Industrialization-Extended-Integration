package com.breakinblocks.miei.chemical.mekanism;

import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

public final class ChemicalHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final boolean input;
    private final ChemicalTank tank;

    public ChemicalHatchBlockEntity(BEP bep, String id, boolean input, long capacity) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
        this.input = input;
        this.tank = new ChemicalTank(input, capacity, this::setChanged);
        registerComponents(tank);
    }

    public boolean isInput() {
        return input;
    }

    public ChemicalTank tank() {
        return tank;
    }

    @Override
    public HatchType getHatchType() {
        return input ? ChemicalHatches.INPUT : ChemicalHatches.OUTPUT;
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
        ChemicalStack stored = tank.getChemicalInTank(0);
        if (stored.isEmpty()) {
            tag.putBoolean(HatchKeys.CHEMICAL_EMPTY, true);
            return;
        }
        ResourceLocation id = MekanismAPI.CHEMICAL_REGISTRY.getKey(stored.getChemical());
        tag.putString(HatchKeys.CHEMICAL_TYPE, "chemical." + id.getNamespace() + "." + id.getPath());
        tag.putLong(HatchKeys.CHEMICAL_AMOUNT, stored.getAmount());
        tag.putLong(HatchKeys.CHEMICAL_CAPACITY, tank.getChemicalTankCapacity(0));
    }
}
