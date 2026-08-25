package com.breakinblocks.miei.heat.pneumaticcraft;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.heat.HeatCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

public final class HeatCrafting {
    private HeatCrafting() {
    }

    public static List<HeatHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<HeatHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof HeatHatchBlockEntity heatHatch) {
                out.add(heatHatch);
            }
        }
        return out;
    }

    public static List<HeatHatchBlockEntity> inRange(MachineBlockEntity machine, HeatCondition condition) {
        List<HeatHatchBlockEntity> out = new ArrayList<>();
        for (HeatHatchBlockEntity hatch : hatches(machine)) {
            if (condition.inRange(hatch.exchanger().temperature())) {
                out.add(hatch);
            }
        }
        return out;
    }

    public static boolean canProcess(MachineBlockEntity machine, HeatCondition condition) {
        return !inRange(machine, condition).isEmpty();
    }

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate) {
        for (HeatCondition condition : conditions(recipe)) {
            List<HeatHatchBlockEntity> usable = inRange(machine, condition);
            if (usable.isEmpty()) {
                return false;
            }
            if (!simulate && condition.heat() > 0) {
                double share = condition.heat() / usable.size();
                for (HeatHatchBlockEntity hatch : usable) {
                    hatch.exchanger().draw(share);
                }
            }
        }
        return true;
    }

    private static List<HeatCondition> conditions(MachineRecipe recipe) {
        List<HeatCondition> out = new ArrayList<>();
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof HeatCondition heat) {
                out.add(heat);
            }
        }
        return out;
    }
}
