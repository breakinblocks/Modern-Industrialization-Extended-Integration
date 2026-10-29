package com.breakinblocks.miei.mixin.client;

import aztech.modern_industrialization.compat.rei.machines.MachineCategoryParams;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.energy.NoEnergyCrafting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MachineCategoryParams.class, remap = false)
public abstract class MachineCategoryParamsNoEnergyMixin {
    @Inject(method = "getRecipeTooltip", at = @At("HEAD"), cancellable = true)
    private void miei$conditionsOnly(MachineRecipe recipe, CallbackInfoReturnable<List<Component>> cir) {
        if (!NoEnergyCrafting.isFree(recipe)) {
            return;
        }
        List<Component> lines = new ArrayList<>();
        for (MachineProcessCondition condition : recipe.conditions) {
            condition.appendDescription(lines);
        }
        cir.setReturnValue(lines);
    }
}
