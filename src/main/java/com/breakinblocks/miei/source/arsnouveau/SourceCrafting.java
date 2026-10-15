package com.breakinblocks.miei.source.arsnouveau;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import com.breakinblocks.miei.source.SourceCondition;
import java.util.ArrayList;
import java.util.List;

public final class SourceCrafting {
    private SourceCrafting() {}

    public static List<SourceHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<SourceHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof SourceHatchBlockEntity sourceHatch) {
                out.add(sourceHatch);
            }
        }
        return out;
    }

    public static int available(List<SourceHatchBlockEntity> hatches) {
        int total = 0;
        for (SourceHatchBlockEntity hatch : hatches) {
            total += hatch.buffer().stored();
        }
        return total;
    }

    public static boolean canProcess(MachineBlockEntity machine, SourceCondition condition) {
        return available(hatches(machine)) >= condition.amount();
    }

    private static int needed(MachineRecipe recipe) {
        int needed = 0;
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof SourceCondition source) {
                needed += source.amount();
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
        List<SourceHatchBlockEntity> hatches = hatches(machine);
        if (available(hatches) < needed) {
            return false;
        }
        if (simulate) {
            return true;
        }
        int remaining = (int) needed;
        for (SourceHatchBlockEntity hatch : hatches) {
            remaining -= hatch.buffer().take(remaining);
            if (remaining <= 0) {
                break;
            }
        }
        return true;
    }
}
