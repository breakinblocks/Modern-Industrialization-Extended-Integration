package com.breakinblocks.miei.particle.nuclearcraft;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.RunningCheck;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import com.breakinblocks.miei.particle.ParticleCondition;
import igentuman.nc.api.particle.ParticleStack;
import java.util.ArrayList;
import java.util.List;

public final class ParticleCrafting {
    private ParticleCrafting() {}

    public static List<ParticleHatchBlockEntity> hatches(MachineBlockEntity machine) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<ParticleHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof ParticleHatchBlockEntity particleHatch) {
                out.add(particleHatch);
            }
        }
        return out;
    }

    public static boolean canRun(MachineBlockEntity machine, ParticleCondition condition) {
        long amount = amount(machine, condition);
        return amount > 0 && amount >= condition.minAmount() * RunningCheck.multiplier();
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        long max = Integer.MAX_VALUE;
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof ParticleCondition particle && particle.minAmount() > 0) {
                max = Math.min(max, amount(machine, particle) / particle.minAmount());
            }
        }
        return (int) max;
    }

    private static long amount(MachineBlockEntity machine, ParticleCondition condition) {
        long total = 0;
        for (ParticleHatchBlockEntity hatch : hatches(machine)) {
            ParticleStack beam = hatch.beam();
            if (!beam.isEmpty() && condition.accepts(beam.particleId(), beam.meanEnergyKeV(), beam.focus())) {
                total += beam.amount();
                if (total < 0) {
                    return Long.MAX_VALUE;
                }
            }
        }
        return total;
    }
}
