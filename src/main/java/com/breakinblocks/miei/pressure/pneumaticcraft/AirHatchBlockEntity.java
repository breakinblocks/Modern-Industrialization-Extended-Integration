package com.breakinblocks.miei.pressure.pneumaticcraft;

import java.util.List;

import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;

import me.desht.pneumaticcraft.api.pressure.PressureTier;
import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class AirHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final AirTank tank;
    private final MIInventory inventory;

    public AirHatchBlockEntity(BEP bep, String id, PressureTier tier, int volume) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
        this.tank = new AirTank(tier, volume);
        registerComponents(tank);

        ConfigurableItemStack upgradeSlot = ConfigurableItemStack.lockedInputSlot(ModUpgrades.SECURITY.get().getItem());
        this.inventory = new MIInventory(List.of(upgradeSlot), List.of(),
            new SlotPositions.Builder().addSlot(80, 40).build(), SlotPositions.empty());
        registerComponents(inventory);
    }

    public AirTank tank() {
        return tank;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) {
            return;
        }
        tank.setSecured(!inventory.getItemStacks().get(0).isEmpty());
        if (!tank.isSecured() && tank.isOverPressure()) {
            rupture();
            return;
        }
        tank.tick(this);
    }

    private void rupture() {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0, 0, 0, 0);
        }
        level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4f, 1f);
        level.destroyBlock(worldPosition, true);
    }

    @Override
    public HatchType getHatchType() {
        return AirHatches.TYPE;
    }

    @Override
    public boolean upgradesToSteel() {
        return false;
    }

    @Override
    public MIInventory getInventory() {
        return inventory;
    }

    @Override
    public void addJadeData(CompoundTag tag) {
        tag.putInt(HatchKeys.AIR, tank.air());
        tag.putFloat(HatchKeys.PRESSURE, tank.pressure());
        tag.putFloat(HatchKeys.DANGER_PRESSURE, tank.dangerPressure());
        tag.putBoolean(HatchKeys.SECURED, tank.isSecured());
    }
}
