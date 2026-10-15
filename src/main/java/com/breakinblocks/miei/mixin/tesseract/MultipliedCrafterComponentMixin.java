package com.breakinblocks.miei.mixin.tesseract;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.breakinblocks.miei.RecipeResources;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.swedz.tesseract.neoforge.compat.mi.component.craft.multiplied.MultipliedCrafterComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultipliedCrafterComponent.class, remap = false)
public abstract class MultipliedCrafterComponentMixin {
    @Inject(
            method = "calculateMultiplier(Lnet/minecraft/world/item/crafting/RecipeHolder;)I",
            at = @At("RETURN"),
            cancellable = true)
    private void miei$capMultiplier(RecipeHolder<MachineRecipe> recipe, CallbackInfoReturnable<Integer> cir) {
        int cap = RecipeResources.maxCrafts(
                ((ModularCrafterComponentAccessor) (Object) this)
                        .miei$getConditionContext()
                        .getBlockEntity(),
                recipe.value());
        if (cap < cir.getReturnValueI()) {
            cir.setReturnValue(Math.max(1, cap));
        }
    }
}
