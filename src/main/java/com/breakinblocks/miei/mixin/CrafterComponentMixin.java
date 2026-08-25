package com.breakinblocks.miei.mixin;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chemical.mekanism.ChemicalCrafting;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatCrafting;
import com.breakinblocks.miei.matter.replication.MatterCrafting;
import com.breakinblocks.miei.pressure.pneumaticcraft.PressureCrafting;
import com.breakinblocks.miei.stress.StressCrafting;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.world.item.crafting.RecipeHolder;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CrafterComponent.class, remap = false)
public abstract class CrafterComponentMixin {
    @Shadow
    @Final
    private MachineProcessCondition.Context conditionContext;

    @Shadow
    private RecipeHolder<MachineRecipe> activeRecipe;

    @Inject(method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z", at = @At("RETURN"), cancellable = true)
    private void miei$takeMatter(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && MIEI.REPLICATION
            && !MatterCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z", at = @At("RETURN"), cancellable = true)
    private void miei$takeAir(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && MIEI.PNEUMATIC
            && !PressureCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z", at = @At("RETURN"), cancellable = true)
    private void miei$takeHeat(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && MIEI.PNEUMATIC
            && !HeatCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z", at = @At("RETURN"), cancellable = true)
    private void miei$takeChemical(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && MIEI.MEKANISM
            && !ChemicalCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZ)Z", at = @At("RETURN"), cancellable = true)
    private void miei$putMatter(MachineRecipe recipe, boolean simulate, boolean toRegister, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && MIEI.REPLICATION
            && !MatterCrafting.put(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZ)Z", at = @At("RETURN"), cancellable = true)
    private void miei$putChemical(MachineRecipe recipe, boolean simulate, boolean toRegister, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && MIEI.MEKANISM
            && !ChemicalCrafting.put(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tickRecipe()Z", at = @At("RETURN"))
    private void miei$tickStress(CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe running = cir.getReturnValueZ() && activeRecipe != null ? activeRecipe.value() : null;
        StressCrafting.updateDemand(conditionContext.getBlockEntity(), running);
    }
}
