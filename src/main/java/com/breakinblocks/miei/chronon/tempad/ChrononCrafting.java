package com.breakinblocks.miei.chronon.tempad;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.chronon.ChrononCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import java.util.ArrayList;
import java.util.List;

public final class ChrononCrafting {
    private ChrononCrafting() {}

    public static List<ChrononHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<ChrononHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof ChrononHatchBlockEntity chrononHatch) {
                out.add(chrononHatch);
            }
        }
        return out;
    }

    public static int available(List<ChrononHatchBlockEntity> hatches) {
        int total = 0;
        for (ChrononHatchBlockEntity hatch : hatches) {
            total += hatch.buffer().stored();
        }
        return total;
    }

    public static boolean canProcess(MachineBlockEntity machine, ChrononCondition condition) {
        return available(hatches(machine)) >= condition.amount();
    }

    private static int needed(MachineRecipe recipe) {
        int needed = 0;
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof ChrononCondition chronon) {
                needed += chronon.amount();
            }
        }
        return needed;
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        int needed = needed(recipe);
        if (needed <= 0) {
            return Integer.MAX_VALUE;
        }
        return available(hatches(machine)) / needed;
    }

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate, int multiplier) {
        long needed = (long) needed(recipe) * multiplier;
        if (needed <= 0) {
            return true;
        }
        List<ChrononHatchBlockEntity> hatches = hatches(machine);
        if (available(hatches) < needed) {
            return false;
        }
        if (simulate) {
            return true;
        }
        int remaining = (int) needed;
        for (ChrononHatchBlockEntity hatch : hatches) {
            hatch.link().validate();
            remaining -= hatch.buffer().take(remaining);
            if (remaining <= 0) {
                break;
            }
        }
        return true;
    }
}
