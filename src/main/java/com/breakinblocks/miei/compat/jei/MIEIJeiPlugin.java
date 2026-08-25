package com.breakinblocks.miei.compat.jei;

import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chemical.mekanism.ChemicalHatches;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatHatches;
import com.breakinblocks.miei.matter.replication.MatterHatches;
import com.breakinblocks.miei.pressure.pneumaticcraft.AirHatches;
import com.breakinblocks.miei.stress.StressHatches;
import com.breakinblocks.miei.stress.create.CreateSupport;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class MIEIJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return MIEI.id("jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (MIEI.REPLICATION) {
            info(registration, MatterHatches.stacks(), "matter");
        }
        if (MIEI.CREATE) {
            info(registration, StressHatches.stacks(), "stress");
            info(registration, List.of(new ItemStack(CreateSupport.COUPLER.get())), "coupler");
        }
        if (MIEI.PNEUMATIC) {
            info(registration, AirHatches.stacks(), "air");
            info(registration, HeatHatches.stacks(), "heat");
        }
        if (MIEI.MEKANISM) {
            info(registration, ChemicalHatches.stacks(), "chemical");
        }
    }

    private static void info(IRecipeRegistration registration, List<ItemStack> stacks, String key) {
        if (!stacks.isEmpty()) {
            registration.addIngredientInfo(stacks, VanillaTypes.ITEM_STACK,
                Component.translatable("miei.jei.info." + key));
        }
    }
}
