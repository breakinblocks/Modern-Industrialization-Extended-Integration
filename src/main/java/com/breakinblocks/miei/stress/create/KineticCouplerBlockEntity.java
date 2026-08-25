package com.breakinblocks.miei.stress.create;

import com.breakinblocks.miei.stress.StressHatchBlockEntity;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KineticCouplerBlockEntity extends KineticBlockEntity {
    private float appliedImpact;

    public KineticCouplerBlockEntity(BlockPos pos, BlockState state) {
        super(CreateSupport.COUPLER_BE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) {
            return;
        }
        StressHatchBlockEntity hatch = hatch();
        if (hatch != null) {
            hatch.link(getSpeed(), spare(), networkId());
        }
        float impact = calculateStressApplied();
        if (impact != appliedImpact) {
            appliedImpact = impact;
            if (hasNetwork()) {
                getOrCreateNetwork().updateStressFor(this, impact);
            }
        }
    }

    @Override
    public float calculateStressApplied() {
        StressHatchBlockEntity hatch = hatch();
        float rotation = Math.abs(getTheoreticalSpeed());
        float impact = hatch == null || rotation < 1 ? 0 : (float) (hatch.demand() / rotation);
        this.lastStressApplied = impact;
        return impact;
    }

    public StressHatchBlockEntity hatch() {
        if (level == null) {
            return null;
        }
        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);
        BlockEntity neighbour = level.getBlockEntity(worldPosition.relative(facing));
        return neighbour instanceof StressHatchBlockEntity hatch ? hatch : null;
    }

    private long networkId() {
        if (!hasNetwork()) {
            return 0L;
        }
        KineticNetwork network = getOrCreateNetwork();
        return network.id == null ? 0L : network.id;
    }

    private double spare() {
        double own = appliedImpact * Math.abs(getTheoreticalSpeed());
        return Math.max(0, capacity - stress + own);
    }
}
