package com.breakinblocks.miei.mixin;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.guicomponents.CraftingMultiblockGui;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.breakinblocks.miei.energy.NoEnergyCrafting;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CraftingMultiblockGui.class, remap = false)
public abstract class CraftingMultiblockGuiMixin {
    @Shadow
    @Final
    private CrafterComponent crafter;

    @ModifyReturnValue(
            method = "extractData()Laztech/modern_industrialization/machines/guicomponents/CraftingMultiblockGui$Data;",
            at = @At("RETURN"))
    private CraftingMultiblockGui.Data miei$hideFreeRecipeEu(CraftingMultiblockGui.Data data) {
        RecipeHolder<MachineRecipe> active = ((CrafterComponentAccessor) crafter).miei$getActiveRecipe();
        if (active == null
                || !NoEnergyCrafting.isFree(active.value())
                || data.activeRecipe().isEmpty()) {
            return data;
        }
        return new CraftingMultiblockGui.Data(
                data.isShapeValid(),
                data.matchesMultipleRecipes(),
                data.activeRecipe()
                        .map(recipe -> new CraftingMultiblockGui.RecipeData(
                                recipe.progress(), recipe.efficiencyTicks(), recipe.maxEfficiencyTicks(), 0, 0)),
                data.remainingOverclockTicks());
    }
}
