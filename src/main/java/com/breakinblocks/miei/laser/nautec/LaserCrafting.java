package com.breakinblocks.miei.laser.nautec;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.RunningCheck;
import com.breakinblocks.miei.laser.LaserCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import com.breakinblocks.nautec.api.blockentities.LaserBlockEntity;
import java.util.ArrayList;
import java.util.List;

public final class LaserCrafting {
    private LaserCrafting() {}

    public static List<LaserHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<LaserHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof LaserHatchBlockEntity laserHatch) {
                out.add(laserHatch);
            }
        }
        return out;
    }

    public static boolean canRun(MachineBlockEntity machine, LaserCondition condition) {
        Beam beam = beam(machine);
        return beam.power() > 0
                && beam.power() >= (long) condition.power() * RunningCheck.multiplier()
                && beam.purity() >= condition.purity();
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        long max = Integer.MAX_VALUE;
        Beam beam = null;
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof LaserCondition laser) {
                if (beam == null) {
                    beam = beam(machine);
                }
                if (beam.purity() < laser.purity()) {
                    return 0;
                }
                max = Math.min(max, beam.power() / laser.power());
            }
        }
        return (int) max;
    }

    private static Beam beam(MachineBlockEntity machine) {
        long power = 0;
        float purity = 0;
        for (LaserHatchBlockEntity hatch : hatches(machine)) {
            int received = hatch.power();
            if (received <= 0) {
                continue;
            }
            purity = LaserBlockEntity.mergedPurity(power, purity, received, hatch.purity());
            power += received;
        }
        return new Beam(power, purity);
    }

    private record Beam(long power, float purity) {}
}
