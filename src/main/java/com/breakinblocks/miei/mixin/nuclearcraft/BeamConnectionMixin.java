package com.breakinblocks.miei.mixin.nuclearcraft;

import com.breakinblocks.miei.particle.nuclearcraft.ParticleHatches;
import igentuman.nc.particle.BeamConnection;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BeamConnection.class, remap = false)
public abstract class BeamConnectionMixin {
    @ModifyVariable(method = "scan", at = @At("HEAD"), argsOnly = true, index = 5)
    private static Predicate<BlockState> miei$acceptHatchAsReceiver(Predicate<BlockState> original) {
        return original.or(state -> ParticleHatches.isHatch(state.getBlock()));
    }

    @ModifyVariable(method = "scan", at = @At("HEAD"), argsOnly = true, index = 6)
    private static Predicate<BlockState> miei$acceptHatchFromAnySide(Predicate<BlockState> original) {
        return original.or(state -> ParticleHatches.isHatch(state.getBlock()));
    }
}
