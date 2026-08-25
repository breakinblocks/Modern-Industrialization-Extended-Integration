package com.breakinblocks.miei.compat.jade;

import com.breakinblocks.miei.compat.HatchInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

final class HatchDataProvider implements IServerDataProvider<BlockAccessor> {
    private final ResourceLocation uid;

    HatchDataProvider(ResourceLocation uid) {
        this.uid = uid;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof HatchInfo hatch) {
            hatch.addJadeData(tag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return uid;
    }
}
