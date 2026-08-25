package com.breakinblocks.miei.chemical.mekanism;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.chemical.ChemicalCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;

import net.minecraft.core.Holder;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

public final class ChemicalCrafting {
    private ChemicalCrafting() {
    }

    public static Holder<Chemical> chemical(ChemicalCondition condition) {
        return MekanismAPI.CHEMICAL_REGISTRY.getHolder(condition.chemical()).orElse(null);
    }

    public static List<ChemicalHatchBlockEntity> hatches(MachineBlockEntity machine, boolean input) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<ChemicalHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof ChemicalHatchBlockEntity chemicalHatch && chemicalHatch.isInput() == input) {
                out.add(chemicalHatch);
            }
        }
        return out;
    }

    public static boolean canProcess(MachineBlockEntity machine, ChemicalCondition condition) {
        if (chemical(condition) == null) {
            return false;
        }
        return condition.output()
            ? space(machine, condition) >= condition.amount()
            : available(machine, condition) >= condition.amount();
    }

    public static long available(MachineBlockEntity machine, ChemicalCondition condition) {
        Holder<Chemical> chemical = chemical(condition);
        if (chemical == null) {
            return 0;
        }
        long total = 0;
        for (ChemicalHatchBlockEntity hatch : hatches(machine, true)) {
            total += hatch.tank().amountOf(chemical);
        }
        return total;
    }

    public static long space(MachineBlockEntity machine, ChemicalCondition condition) {
        Holder<Chemical> chemical = chemical(condition);
        if (chemical == null) {
            return 0;
        }
        long total = 0;
        for (ChemicalHatchBlockEntity hatch : hatches(machine, false)) {
            total += hatch.tank().spaceFor(chemical);
        }
        return total;
    }

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate) {
        for (ChemicalCondition condition : conditions(recipe, false)) {
            if (chemical(condition) == null || available(machine, condition) < condition.amount()) {
                return false;
            }
            if (!simulate) {
                long remaining = condition.amount();
                for (ChemicalHatchBlockEntity hatch : hatches(machine, true)) {
                    remaining -= hatch.tank().take(chemical(condition), remaining);
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        return true;
    }

    public static boolean put(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate) {
        for (ChemicalCondition condition : conditions(recipe, true)) {
            if (chemical(condition) == null || space(machine, condition) < condition.amount()) {
                return false;
            }
            if (!simulate) {
                long remaining = condition.amount();
                for (ChemicalHatchBlockEntity hatch : hatches(machine, false)) {
                    remaining -= hatch.tank().add(chemical(condition), remaining);
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        return true;
    }

    private static List<ChemicalCondition> conditions(MachineRecipe recipe, boolean output) {
        List<ChemicalCondition> out = new ArrayList<>();
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof ChemicalCondition chemical && chemical.output() == output) {
                out.add(chemical);
            }
        }
        return out;
    }
}
