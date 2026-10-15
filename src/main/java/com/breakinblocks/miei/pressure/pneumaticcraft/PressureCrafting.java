package com.breakinblocks.miei.pressure.pneumaticcraft;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import com.breakinblocks.miei.pressure.PressureCondition;
import java.util.ArrayList;
import java.util.List;

public final class PressureCrafting {
    private PressureCrafting() {}

    public static List<AirHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<AirHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof AirHatchBlockEntity airHatch) {
                out.add(airHatch);
            }
        }
        return out;
    }

    public static boolean canProcess(MachineBlockEntity machine, PressureCondition condition) {
        return available(machine, condition) >= condition.air();
    }

    public static int available(MachineBlockEntity machine, PressureCondition condition) {
        int total = 0;
        for (AirHatchBlockEntity hatch : hatches(machine)) {
            if (hatch.tank().pressure() >= condition.pressure()) {
                total += hatch.tank().air();
            }
        }
        return total;
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        int max = Integer.MAX_VALUE;
        for (PressureCondition condition : conditions(recipe)) {
            if (condition.air() > 0) {
                max = Math.min(max, available(machine, condition) / condition.air());
            }
        }
        return max;
    }

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate, int multiplier) {
        for (PressureCondition condition : conditions(recipe)) {
            long air = (long) condition.air() * multiplier;
            if (available(machine, condition) < air) {
                return false;
            }
            if (!simulate) {
                int remaining = (int) air;
                for (AirHatchBlockEntity hatch : hatches(machine)) {
                    if (hatch.tank().pressure() < condition.pressure()) {
                        continue;
                    }
                    remaining -= hatch.tank().take(remaining);
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        return true;
    }

    private static List<PressureCondition> conditions(MachineRecipe recipe) {
        List<PressureCondition> out = new ArrayList<>();
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof PressureCondition pressure) {
                out.add(pressure);
            }
        }
        return out;
    }
}
