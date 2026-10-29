package com.breakinblocks.miei.mixin.client;

import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory;
import aztech.modern_industrialization.client.compat.viewer.usage.MachineCategory;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.breakinblocks.miei.energy.NoEnergyCrafting;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MachineCategory.class, remap = false)
public abstract class MachineCategoryNoEnergyMixin {
    private static final String BUILD_WIDGETS =
            "buildWidgets(Lnet/minecraft/world/item/crafting/RecipeHolder;Laztech/modern_industrialization/client/compat/viewer/abstraction/ViewerCategory$WidgetList;)V";

    @WrapOperation(
            method = BUILD_WIDGETS,
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Laztech/modern_industrialization/util/TextHelper;getEuTextTick(J)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent miei$noEnergyText(
            long eu, Operation<MutableComponent> original, @Local(argsOnly = true) RecipeHolder<MachineRecipe> recipe) {
        if (NoEnergyCrafting.isFree(recipe.value())) {
            return Component.translatable("miei.jei.no_energy");
        }
        return original.call(eu);
    }

    @WrapOperation(
            method = BUILD_WIDGETS,
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Laztech/modern_industrialization/client/compat/viewer/abstraction/ViewerCategory$WidgetList;drawable(Ljava/util/function/Consumer;)V",
                            ordinal = 1))
    private void miei$noEnergyIcon(
            ViewerCategory.WidgetList widgets,
            Consumer<GuiGraphics> drawable,
            Operation<Void> original,
            @Local(argsOnly = true) RecipeHolder<MachineRecipe> recipe) {
        if (!NoEnergyCrafting.isFree(recipe.value())) {
            original.call(widgets, drawable);
        }
    }
}
