package com.breakinblocks.miei.matter.replication;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

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
        List<MatterStack> contents = tank.contents();
        if (contents.isEmpty()) {
            tag.putBoolean(HatchKeys.MATTER_EMPTY, true);
            return;
        }
        ListTag list = new ListTag();
        for (MatterStack stack : contents) {
            CompoundTag entry = new CompoundTag();
            entry.putString(
                    HatchKeys.MATTER_TYPE,
                    "miei.matter." + stack.getMatterType().getName().toLowerCase());
            entry.putDouble(HatchKeys.MATTER_AMOUNT, stack.getAmount());
            list.add(entry);
        }
        tag.put(HatchKeys.MATTER_LIST, list);
        tag.putDouble(HatchKeys.MATTER_CAPACITY, tank.capacity());
    }
}
