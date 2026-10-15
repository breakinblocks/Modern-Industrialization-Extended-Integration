package com.breakinblocks.miei.mixin;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.CrafterComponent.Behavior;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import aztech.modern_industrialization.util.Simulation;
import com.breakinblocks.miei.RecipeResources;
import com.breakinblocks.miei.RunningCheck;
import com.breakinblocks.miei.energy.NoEnergyCrafting;
import com.breakinblocks.miei.stress.StressCrafting;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CrafterComponent.class, remap = false)
public abstract class CrafterComponentMixin {
    @Shadow
    @Final
    private MachineProcessCondition.Context conditionContext;

    @Shadow
    @Final
    private Behavior behavior;

    @Shadow
    private RecipeHolder<MachineRecipe> activeRecipe;

    @Inject(
            method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$takeResources(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && !RecipeResources.take(conditionContext.getBlockEntity(), recipe, simulate, 1)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZ)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$putResources(
            MachineRecipe recipe, boolean simulate, boolean toRegister, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && !RecipeResources.put(conditionContext.getBlockEntity(), recipe, simulate, 1)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tickRecipe()Z", at = @At("RETURN"))
    private void miei$tickStress(CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe running = cir.getReturnValueZ() && activeRecipe != null ? activeRecipe.value() : null;
        StressCrafting.updateDemand(conditionContext.getBlockEntity(), running, 1);
    }

    @WrapOperation(
            method = "tickRecipe()Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Laztech/modern_industrialization/machines/recipe/MachineRecipe;conditionsMatch(Laztech/modern_industrialization/machines/recipe/condition/MachineProcessCondition$Context;)Z"))
    private boolean miei$conditionsWhileRunning(
            MachineRecipe recipe, MachineProcessCondition.Context context, Operation<Boolean> original) {
        return RunningCheck.run(() -> original.call(recipe, context));
    }

    @Redirect(
            method = "tickRecipe()Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Laztech/modern_industrialization/machines/components/CrafterComponent$Behavior;consumeEu(JLaztech/modern_industrialization/util/Simulation;)J"))
    private long miei$consumeEu(Behavior target, long amount, Simulation simulation) {
        if (simulation == Simulation.SIMULATE) {
            long available = target.consumeEu(amount, simulation);
            if (available >= amount) {
                return available;
            }
            return NoEnergyCrafting.hasFreeRecipe(target.getCrafterWorld(), target.recipeType()) ? amount : available;
        }
        if (activeRecipe != null && NoEnergyCrafting.isFree(activeRecipe.value())) {
            return amount;
        }
        return target.consumeEu(amount, simulation);
    }

    @Inject(
            method = "canStartRecipe(Laztech/modern_industrialization/machines/recipe/MachineRecipe;)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$requireEnergy(MachineRecipe recipe, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && !NoEnergyCrafting.isFree(recipe)
                && behavior.consumeEu(1, Simulation.SIMULATE) < 1) {
            cir.setReturnValue(false);
        }
    }
}
