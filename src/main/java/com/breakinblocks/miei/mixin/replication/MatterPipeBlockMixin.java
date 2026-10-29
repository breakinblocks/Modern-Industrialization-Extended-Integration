package com.breakinblocks.miei.mixin.replication;

import com.breakinblocks.miei.matter.replication.MatterHatches;
import com.buuz135.replication.block.MatterPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MatterPipeBlock.class, remap = false)
public abstract class MatterPipeBlockMixin {
    @Inject(method = "getConnectionType", at = @At("HEAD"), cancellable = true)
    private void miei$connectToMatterHatches(
            Level level, BlockPos pos, Direction direction, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!level.isClientSide()
                && MatterHatches.isHatch(
                        level.getBlockState(pos.relative(direction)).getBlock())) {
            cir.setReturnValue(true);
        }
    }
}
