package com.breakinblocks.miei.mixin.tesseract;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition.Context;
import aztech.modern_industrialization.util.Simulation;
import com.breakinblocks.miei.RecipeResources;
import com.breakinblocks.miei.RunningCheck;
import com.breakinblocks.miei.energy.NoEnergyCrafting;
import com.breakinblocks.miei.stress.StressCrafting;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.AbstractModularCrafterComponent;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.ModularCrafterAccessBehavior;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.MultipliedCrafterComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractModularCrafterComponent.class, remap = false)
public abstract class ModularCrafterComponentMixin {
    @Shadow
    @Final
    protected Context conditionContext;

    @Shadow
    @Final
    protected ModularCrafterAccessBehavior behavior;

    @Shadow
    protected Object activeRecipe;

    @Inject(method = "takeInputs(Ljava/lang/Object;Z)Z", at = @At("RETURN"), cancellable = true)
    private void miei$takeResources(Object recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe machineRecipe = miei$recipe(recipe);
        if (cir.getReturnValueZ()
                && machineRecipe != null
                && !RecipeResources.take(
                        conditionContext.getBlockEntity(), machineRecipe, simulate, miei$multiplier())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "putOutputs(Ljava/lang/Object;ZZ)Z", at = @At("RETURN"), cancellable = true)
    private void miei$putResources(
            Object recipe, boolean simulate, boolean toggleLock, CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe machineRecipe = miei$recipe(recipe);
        if (cir.getReturnValueZ()
                && machineRecipe != null
                && !RecipeResources.put(
                        conditionContext.getBlockEntity(), machineRecipe, simulate, miei$multiplier())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canStartRecipe(Ljava/lang/Object;Z)Z", at = @At("RETURN"), cancellable = true)
    private void miei$requireEnergy(Object recipe, boolean ignoreConditions, CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe machineRecipe = miei$recipe(recipe);
        if (cir.getReturnValueZ()
                && machineRecipe != null
                && !NoEnergyCrafting.isFree(machineRecipe)
                && !behavior.canConsumeEu(1)) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(
            method = "tickRecipe()Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/swedz/tesseract/neoforge/compat/mi/component/craft/AbstractModularCrafterComponent;doConditionsMatchForRecipe(Ljava/lang/Object;)Z"))
    private boolean miei$conditionsWhileRunning(
            AbstractModularCrafterComponent<?> crafter, Object recipe, Operation<Boolean> original) {
        return RunningCheck.run(miei$multiplier(), () -> original.call(crafter, recipe));
    }

    @WrapOperation(
            method = "tickRecipe()Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/swedz/tesseract/neoforge/compat/mi/component/craft/ModularCrafterAccessBehavior;canConsumeEu(J)Z"))
    private boolean miei$canConsumeEu(ModularCrafterAccessBehavior target, long amount, Operation<Boolean> original) {
        if (original.call(target, amount)) {
            return true;
        }
        if ((Object) this instanceof MultipliedCrafterComponent crafter) {
            MachineRecipeType type = crafter.getRecipeType();
            return type != null && NoEnergyCrafting.hasFreeRecipe(target.getCrafterWorld(), type);
        }
        return false;
    }

    @WrapOperation(
            method = "tickRecipe()Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/swedz/tesseract/neoforge/compat/mi/component/craft/ModularCrafterAccessBehavior;consumeEu(JLaztech/modern_industrialization/util/Simulation;)J"))
    private long miei$consumeEu(
            ModularCrafterAccessBehavior target, long amount, Simulation simulation, Operation<Long> original) {
        MachineRecipe machineRecipe = miei$recipe(activeRecipe);
        if (simulation == Simulation.ACT && machineRecipe != null && NoEnergyCrafting.isFree(machineRecipe)) {
            return amount;
        }
        return original.call(target, amount, simulation);
    }

    @Inject(method = "tickRecipe()Z", at = @At("RETURN"))
    private void miei$tickStress(CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe running = cir.getReturnValueZ() ? miei$recipe(activeRecipe) : null;
        StressCrafting.updateDemand(conditionContext.getBlockEntity(), running, miei$multiplier());
    }

    @Unique
    private int miei$multiplier() {
        if ((Object) this instanceof MultipliedCrafterComponent crafter) {
            int pending = ((MultipliedCrafterComponentAccessor) (Object) crafter).miei$getTryRecipeMultiplier();
            return Math.max(1, pending != 0 ? pending : crafter.getRecipeMultiplier());
        }
        return 1;
    }

    @Unique
    private static MachineRecipe miei$recipe(Object recipe) {
        if (recipe instanceof RecipeHolder<?> holder && holder.value() instanceof MachineRecipe machineRecipe) {
            return machineRecipe;
        }
        return null;
    }
}
