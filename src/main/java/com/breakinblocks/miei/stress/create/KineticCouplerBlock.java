package com.breakinblocks.miei.stress.create;

import com.breakinblocks.miei.stress.StressHatchBlockEntity;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class KineticCouplerBlock extends DirectionalKineticBlock implements IBE<KineticCouplerBlockEntity> {
    public KineticCouplerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction side : Direction.values()) {
            BlockPos neighbour = context.getClickedPos().relative(side);
            if (context.getLevel().getBlockEntity(neighbour) instanceof StressHatchBlockEntity) {
                return defaultBlockState().setValue(FACING, side);
            }
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

    @Override
    public Class<KineticCouplerBlockEntity> getBlockEntityClass() {
        return KineticCouplerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticCouplerBlockEntity> getBlockEntityType() {
        return CreateSupport.COUPLER_BE.get();
    }
}
