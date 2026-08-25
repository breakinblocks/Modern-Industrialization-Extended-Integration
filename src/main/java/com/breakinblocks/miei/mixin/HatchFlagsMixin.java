package com.breakinblocks.miei.mixin;

import java.util.Set;

import com.breakinblocks.miei.MIEI;

import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HatchFlags.class, remap = false)
public abstract class HatchFlagsMixin {
    @Shadow
    @Final
    private Set<HatchType> allowed;

    @Inject(method = "allows", at = @At("HEAD"), cancellable = true)
    private void miei$allowIntegrationHatches(HatchType type, CallbackInfoReturnable<Boolean> cir) {
        if (!allowed.isEmpty() && MIEI.MOD_ID.equals(type.id().getNamespace())) {
            cir.setReturnValue(true);
        }
    }
}
