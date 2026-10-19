package com.breakinblocks.miei.chronon.tempad;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import com.breakinblocks.miei.MIEIConfig;
import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class ChrononHatchBlockEntity extends HatchBlockEntity implements HatchInfo {
    private final ChrononBuffer buffer;
    private final MetronomeLink link;
    private final MIInventory inventory;
    private final IItemHandler itemHandler;
    private final int rate;

    public ChrononHatchBlockEntity(BEP bep, String id, int capacity, int rate) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
        this.buffer = new ChrononBuffer(capacity, this::setChanged);
        this.link = new MetronomeLink(() -> !isRemoved());
        this.rate = rate;
        registerComponents(buffer);

        if (MIEIConfig.chrononItemSlot()) {
            this.inventory = new MIInventory(
                    List.of(ConfigurableItemStack.standardIOSlot(true)),
                    List.of(),
                    new SlotPositions.Builder().addSlot(80, 40).build(),
                    SlotPositions.empty());
            this.itemHandler = new ChrononSlotHandler(inventory.itemStorage.itemHandler);
            registerComponents(inventory);
        } else {
            this.inventory = MIInventory.EMPTY;
            this.itemHandler = null;
        }
    }

    public ChrononBuffer buffer() {
        return buffer;
    }

    public MetronomeLink link() {
        return link;
    }

    public IItemHandler itemHandler() {
        return itemHandler;
    }

    private ConfigurableItemStack slot() {
        List<ConfigurableItemStack> stacks = inventory.getItemStacks();
        return stacks.isEmpty() ? null : stacks.get(0);
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel serverLevel) {
            int drawn = link.draw(serverLevel, worldPosition, buffer, rate);
            ConfigurableItemStack slot = slot();
            if (slot != null) {
                ChrononItems.draw(slot, buffer, rate - drawn);
            }
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
        return inventory;
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
        ConfigurableItemStack slot = slot();
        if (slot == null) {
            return;
        }
        ItemStack stack = ChrononItems.stackOf(slot);
        ChrononHandler handler = ChrononItems.handler(stack);
        if (handler != null) {
            tag.putString(HatchKeys.CHRONON_ITEM, stack.getDescriptionId());
            tag.putInt(HatchKeys.CHRONON_ITEM_POWER, handler.getPower());
            tag.putInt(HatchKeys.CHRONON_ITEM_CAPACITY, handler.getMaxPower());
        }
    }
}
