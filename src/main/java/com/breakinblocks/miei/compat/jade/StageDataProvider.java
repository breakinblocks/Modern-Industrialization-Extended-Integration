package com.breakinblocks.miei.compat.jade;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.stage.WaitingStages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

final class StageDataProvider implements IServerDataProvider<BlockAccessor> {
    static final StageDataProvider INSTANCE = new StageDataProvider();
    private static final ResourceLocation UID = MIEI.id("stage");

    private StageDataProvider() {}

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof MachineBlockEntity machine) {
            WaitingStages.addJadeData(machine, tag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
