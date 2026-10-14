package com.breakinblocks.miei.mixin;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.CrafterComponent.Behavior;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import aztech.modern_industrialization.util.Simulation;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chemical.mekanism.ChemicalCrafting;
import com.breakinblocks.miei.chronon.tempad.ChrononCrafting;
import com.breakinblocks.miei.energy.NoEnergyCrafting;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatCrafting;
import com.breakinblocks.miei.matter.replication.MatterCrafting;
import com.breakinblocks.miei.pressure.pneumaticcraft.PressureCrafting;
import com.breakinblocks.miei.source.arsnouveau.SourceCrafting;
import com.breakinblocks.miei.stress.StressCrafting;
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
    private void miei$takeMatter(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.REPLICATION
                && !MatterCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$takeAir(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.PNEUMATIC
                && !PressureCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$takeHeat(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.PNEUMATIC
                && !HeatCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$takeChemical(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.MEKANISM
                && !ChemicalCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$takeChronons(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.TEMPAD
                && !ChrononCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$takeSource(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.ARS_NOUVEAU
                && !SourceCrafting.take(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZ)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$putMatter(
            MachineRecipe recipe, boolean simulate, boolean toRegister, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.REPLICATION
                && !MatterCrafting.put(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "putItemOutputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;ZZ)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$putChemical(
            MachineRecipe recipe, boolean simulate, boolean toRegister, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()
                && MIEI.MEKANISM
                && !ChemicalCrafting.put(conditionContext.getBlockEntity(), recipe, simulate)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tickRecipe()Z", at = @At("RETURN"))
    private void miei$tickStress(CallbackInfoReturnable<Boolean> cir) {
        MachineRecipe running = cir.getReturnValueZ() && activeRecipe != null ? activeRecipe.value() : null;
        StressCrafting.updateDemand(conditionContext.getBlockEntity(), running);
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
