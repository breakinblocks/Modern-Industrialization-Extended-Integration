package com.breakinblocks.miei;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.breakinblocks.miei.chemical.mekanism.ChemicalCrafting;
import com.breakinblocks.miei.chronon.tempad.ChrononCrafting;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatCrafting;
import com.breakinblocks.miei.matter.replication.MatterCrafting;
import com.breakinblocks.miei.pressure.pneumaticcraft.PressureCrafting;
import com.breakinblocks.miei.source.arsnouveau.SourceCrafting;
import com.breakinblocks.miei.stress.StressCrafting;

public final class RecipeResources {
    private RecipeResources() {}

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate, int multiplier) {
        return (!MIEI.REPLICATION || MatterCrafting.take(machine, recipe, simulate, multiplier))
                && (!MIEI.PNEUMATIC || PressureCrafting.take(machine, recipe, simulate, multiplier))
                && (!MIEI.PNEUMATIC || HeatCrafting.take(machine, recipe, simulate, multiplier))
                && (!MIEI.MEKANISM || ChemicalCrafting.take(machine, recipe, simulate, multiplier))
                && (!MIEI.TEMPAD || ChrononCrafting.take(machine, recipe, simulate, multiplier))
                && (!MIEI.ARS_NOUVEAU || SourceCrafting.take(machine, recipe, simulate, multiplier));
    }

    public static boolean put(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate, int multiplier) {
        return (!MIEI.REPLICATION || MatterCrafting.put(machine, recipe, simulate, multiplier))
                && (!MIEI.MEKANISM || ChemicalCrafting.put(machine, recipe, simulate, multiplier));
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        int max = Integer.MAX_VALUE;
        if (MIEI.REPLICATION) {
            max = Math.min(max, MatterCrafting.maxCrafts(machine, recipe));
        }
        if (MIEI.CREATE) {
            max = Math.min(max, StressCrafting.maxCrafts(machine, recipe));
        }
        if (MIEI.PNEUMATIC) {
            max = Math.min(max, PressureCrafting.maxCrafts(machine, recipe));
        }
        if (MIEI.MEKANISM) {
            max = Math.min(max, ChemicalCrafting.maxCrafts(machine, recipe));
        }
        if (MIEI.TEMPAD) {
            max = Math.min(max, ChrononCrafting.maxCrafts(machine, recipe));
        }
        if (MIEI.ARS_NOUVEAU) {
            max = Math.min(max, SourceCrafting.maxCrafts(machine, recipe));
        }
        return max;
    }
}
