package com.breakinblocks.miei.energy;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class NoEnergyCrafting {
    private static final long RESCAN_MILLIS = 5000;

    private static final Map<MachineRecipeType, Scan> SCANS = new HashMap<>();

    private NoEnergyCrafting() {}

    public static boolean isFree(MachineRecipe recipe) {
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof NoEnergyCondition) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasFreeRecipe(ServerLevel level, MachineRecipeType type) {
        long now = System.currentTimeMillis();
        Scan scan = SCANS.get(type);
        if (scan != null && now < scan.expiry()) {
            return scan.present();
        }
        boolean present = false;
        for (RecipeHolder<MachineRecipe> holder : type.getRecipesWithCache(level)) {
            if (isFree(holder.value())) {
                present = true;
                break;
            }
        }
        SCANS.put(type, new Scan(now + RESCAN_MILLIS, present));
        return present;
    }

    private record Scan(long expiry, boolean present) {}
}
