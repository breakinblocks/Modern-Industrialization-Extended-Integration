package com.breakinblocks.miei.chronon.tempad;

import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public final class ChrononHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final ChrononBuffer buffer;
    private final MetronomeLink link;
    private final int rate;

    public ChrononHatchBlockEntity(BEP bep, String id, int capacity, int rate) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
        this.buffer = new ChrononBuffer(capacity, this::setChanged);
        this.link = new MetronomeLink(() -> !isRemoved());
        this.rate = rate;
        registerComponents(buffer);
    }

    public ChrononBuffer buffer() {
        return buffer;
    }

    public MetronomeLink link() {
        return link;
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel serverLevel) {
            link.draw(serverLevel, worldPosition, buffer, rate);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        link.unlink();
    }

    @Override
    public HatchType getHatchType() {
        return ChrononHatches.TYPE;
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
        tag.putInt(HatchKeys.CHRONON_AMOUNT, buffer.stored());
        tag.putInt(HatchKeys.CHRONON_CAPACITY, buffer.capacity());
        BlockPos source = link.sourcePos();
        if (source != null) {
            tag.putLong(HatchKeys.CHRONON_SOURCE, source.asLong());
        } else if (level != null) {
            tag.putInt(HatchKeys.CHRONON_RADIUS, MetronomeLink.RADIUS);
            tag.putInt(HatchKeys.CHRONON_NEXT_SCAN, link.ticksUntilScan(level.getGameTime()));
        }
    }
}
