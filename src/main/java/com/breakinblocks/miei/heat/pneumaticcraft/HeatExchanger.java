package com.breakinblocks.miei.heat.pneumaticcraft;

import me.desht.pneumaticcraft.api.PneumaticRegistry;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;

import aztech.modern_industrialization.machines.MachineComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public final class HeatExchanger implements MachineComponent.ServerOnly {
    private static final int RECONNECT_INTERVAL = 20;

    private final IHeatExchangerLogic logic;
    private int reconnectCountdown;

    public HeatExchanger(double thermalCapacity) {
        logic = PneumaticRegistry.getInstance().getHeatRegistry().makeHeatExchangerLogic();
        logic.setThermalCapacity(thermalCapacity);
    }

    public IHeatExchangerLogic logic() {
        return logic;
    }

    public void tick(Level level, BlockPos pos) {
        if (--reconnectCountdown <= 0) {
            reconnectCountdown = RECONNECT_INTERVAL;
            logic.initializeAsHull(level, pos, IHeatExchangerLogic.ALL_BLOCKS, Direction.values());
        }
        logic.tick();
    }

    public double temperature() {
        return logic.getTemperature();
    }

    public void draw(double amount) {
        if (amount != 0) {
            logic.addHeat(-amount);
        }
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("heat", logic.serializeNBT());
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean upgrading) {
        Tag stored = tag.get("heat");
        if (stored instanceof CompoundTag compound) {
            logic.deserializeNBT(compound);
        }
    }
}
