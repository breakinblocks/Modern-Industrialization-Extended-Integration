package com.breakinblocks.miei.pressure.pneumaticcraft;

import java.util.List;

import me.desht.pneumaticcraft.api.PneumaticRegistry;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;

import aztech.modern_industrialization.machines.MachineComponent;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class AirTank implements MachineComponent.ServerOnly {
    private final IAirHandlerMachine handler;
    private boolean secured;

    public AirTank(PressureTier tier, int volume) {
        handler = PneumaticRegistry.getInstance().getAirHandlerMachineFactory().createAirHandler(tier, volume);
        handler.setConnectableFaces(List.of(Direction.values()));
    }

    public IAirHandlerMachine handler() {
        return handler;
    }

    public void tick(BlockEntity blockEntity) {
        handler.tick(blockEntity);
    }

    public void setSecured(boolean value) {
        if (value == secured) {
            return;
        }
        secured = value;
        if (secured) {
            handler.enableSafetyVenting(pressure -> pressure > handler.getDangerPressure(), Direction.UP);
        } else {
            handler.disableSafetyVenting();
        }
    }

    public boolean isSecured() {
        return secured;
    }

    public boolean isOverPressure() {
        return handler.getPressure() > handler.getDangerPressure();
    }

    public int air() {
        return Math.max(0, handler.getAir());
    }

    public float pressure() {
        return handler.getPressure();
    }

    public float dangerPressure() {
        return handler.getDangerPressure();
    }

    public int take(int amount) {
        int taken = Math.min(amount, air());
        if (taken > 0) {
            handler.addAir(-taken);
        }
        return taken;
    }

    @Override
    public void writeNbt(CompoundTag tag, Provider registries) {
        tag.put("air", handler.serializeNBT());
    }

    @Override
    public void readNbt(CompoundTag tag, Provider registries, boolean upgrading) {
        Tag stored = tag.get("air");
        if (stored instanceof CompoundTag compound) {
            handler.deserializeNBT(compound);
        }
    }
}
