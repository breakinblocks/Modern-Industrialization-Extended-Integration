package com.breakinblocks.miei.mixin.client;

import aztech.modern_industrialization.machines.guicomponents.CraftingMultiblockGui;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
        targets = "aztech.modern_industrialization.client.machines.guicomponents.CraftingMultiblockGuiClient$Renderer",
        remap = false)
public abstract class CraftingMultiblockGuiNoEnergyMixin {
    private static final String DRAW_WORD_WRAP =
            "Laztech/modern_industrialization/client/machines/guicomponents/CraftingMultiblockGuiClient$Renderer;drawWordWrap(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIIZ)I";

    @WrapOperation(
            method = "renderBackground(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At(value = "INVOKE", target = DRAW_WORD_WRAP, ordinal = 2))
    private int miei$noEnergyBaseLine(
            GuiGraphics graphics,
            Font font,
            Component text,
            int x,
            int y,
            int width,
            int color,
            boolean shadow,
            Operation<Integer> original,
            @Local CraftingMultiblockGui.RecipeData recipe) {
        Component shown = recipe.baseRecipeEu() == 0 ? Component.translatable("miei.jei.no_energy") : text;
        return original.call(graphics, font, shown, x, y, width, color, shadow);
    }

    @WrapOperation(
            method = "renderBackground(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At(value = "INVOKE", target = DRAW_WORD_WRAP, ordinal = 3))
    private int miei$noEnergyCurrentLine(
            GuiGraphics graphics,
            Font font,
            Component text,
            int x,
            int y,
            int width,
            int color,
            boolean shadow,
            Operation<Integer> original,
            @Local CraftingMultiblockGui.RecipeData recipe) {
        if (recipe.baseRecipeEu() == 0) {
            return 0;
        }
        return original.call(graphics, font, text, x, y, width, color, shadow);
    }
}
