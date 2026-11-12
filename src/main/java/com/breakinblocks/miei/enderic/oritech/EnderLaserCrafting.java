package com.breakinblocks.miei.enderic.oritech;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.RunningCheck;
import com.breakinblocks.miei.enderic.EnderLaserCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import java.util.ArrayList;
import java.util.List;

public final class EnderLaserCrafting {
    private EnderLaserCrafting() {}

    public static List<EnderLaserHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<EnderLaserHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof EnderLaserHatchBlockEntity laserHatch) {
                out.add(laserHatch);
            }
        }
        return out;
    }

    public static boolean canRun(MachineBlockEntity machine, EnderLaserCondition condition) {
        long rate = rate(machine);
        return rate > 0 && rate >= condition.minRate() * RunningCheck.multiplier();
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        long max = Integer.MAX_VALUE;
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof EnderLaserCondition laser && laser.minRate() > 0) {
                max = Math.min(max, rate(machine) / laser.minRate());
            }
        }
        return (int) max;
    }

    private static long rate(MachineBlockEntity machine) {
        long total = 0;
        for (EnderLaserHatchBlockEntity hatch : hatches(machine)) {
            total += hatch.rate();
            if (total < 0) {
                return Long.MAX_VALUE;
            }
        }
        return total;
    }
}
