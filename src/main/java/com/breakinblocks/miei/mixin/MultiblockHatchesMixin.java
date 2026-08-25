package com.breakinblocks.miei.mixin;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chemical.mekanism.ChemicalHatches;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatHatches;
import com.breakinblocks.miei.matter.replication.MatterHatches;
import com.breakinblocks.miei.pressure.pneumaticcraft.AirHatches;
import com.breakinblocks.miei.stress.StressHatches;

import aztech.modern_industrialization.machines.init.MultiblockHatches;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiblockHatches.class, remap = false)
public abstract class MultiblockHatchesMixin {
    @Inject(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Laztech/modern_industrialization/compat/kubejs/KubeJSProxy;fireRegisterHatchesEvent()V",
            shift = At.Shift.AFTER))
    private static void miei$registerHatches(CallbackInfo ci) {
        if (MIEI.REPLICATION) {
            MatterHatches.register();
        }
        if (MIEI.CREATE) {
            StressHatches.register();
        }
        if (MIEI.PNEUMATIC) {
            AirHatches.register();
            HeatHatches.register();
        }
        if (MIEI.MEKANISM) {
            ChemicalHatches.register();
        }
    }
}
