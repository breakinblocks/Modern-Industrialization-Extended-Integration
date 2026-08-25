package com.breakinblocks.miei.stress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

public final class StressCrafting {
    private StressCrafting() {
    }

    public static List<StressHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<StressHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof StressHatchBlockEntity stressHatch) {
                out.add(stressHatch);
            }
        }
        return out;
    }

    public static boolean canRun(MachineBlockEntity machine, StressCondition condition) {
        List<StressHatchBlockEntity> hatches = hatches(machine);
        if (hatches.isEmpty()) {
            return false;
        }
        Set<Long> counted = new HashSet<>();
        double spare = 0;
        for (StressHatchBlockEntity hatch : hatches) {
            if (!hatch.isLinked() || Math.abs(hatch.speed()) < Math.max(1, condition.rpm())) {
                continue;
            }
            if (counted.add(hatch.networkId())) {
                spare += hatch.spare();
            }
        }
        return spare >= condition.su();
    }

    public static void updateDemand(MachineBlockEntity machine, MachineRecipe recipe) {
        if (!MIEI.CREATE) {
            return;
        }
        List<StressHatchBlockEntity> hatches = hatches(machine);
        if (hatches.isEmpty()) {
            return;
        }
        double total = 0;
        if (recipe != null) {
            for (MachineProcessCondition condition : recipe.conditions) {
                if (condition instanceof StressCondition stress) {
                    total += stress.su();
                }
            }
        }
        double share = total / hatches.size();
        for (StressHatchBlockEntity hatch : hatches) {
            hatch.setDemand(share);
        }
    }
}
