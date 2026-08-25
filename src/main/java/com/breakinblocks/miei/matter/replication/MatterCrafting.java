package com.breakinblocks.miei.matter.replication;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.matter.MatterCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import com.buuz135.replication.api.MatterType;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

public final class MatterCrafting {
    private MatterCrafting() {
    }

    public static MatterType type(MatterCondition condition) {
        for (MatterType type : MatterType.values()) {
            if (type.getName().equalsIgnoreCase(condition.matter())) {
                return type;
            }
        }
        return MatterType.EMPTY;
    }

    public static List<MatterHatchBlockEntity> hatches(MachineBlockEntity machine, boolean input) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<MatterHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof MatterHatchBlockEntity matterHatch && matterHatch.isInput() == input) {
                out.add(matterHatch);
            }
        }
        return out;
    }

    public static boolean canProcess(MachineBlockEntity machine, MatterCondition condition) {
        return condition.output()
            ? space(machine, condition) >= condition.amount()
            : available(machine, condition) >= condition.amount();
    }

    public static double available(MachineBlockEntity machine, MatterCondition condition) {
        MatterType type = type(condition);
        double total = 0;
        for (MatterHatchBlockEntity hatch : hatches(machine, true)) {
            total += hatch.tank().amountOf(type);
        }
        return total;
    }

    public static double space(MachineBlockEntity machine, MatterCondition condition) {
        MatterType type = type(condition);
        double total = 0;
        for (MatterHatchBlockEntity hatch : hatches(machine, false)) {
            total += hatch.tank().spaceFor(type);
        }
        return total;
    }

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate) {
        for (MatterCondition condition : conditions(recipe, false)) {
            if (available(machine, condition) < condition.amount()) {
                return false;
            }
            if (!simulate) {
                double remaining = condition.amount();
                for (MatterHatchBlockEntity hatch : hatches(machine, true)) {
                    remaining -= hatch.tank().take(type(condition), remaining, false);
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        return true;
    }

    public static boolean put(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate) {
        for (MatterCondition condition : conditions(recipe, true)) {
            if (space(machine, condition) < condition.amount()) {
                return false;
            }
            if (!simulate) {
                double remaining = condition.amount();
                for (MatterHatchBlockEntity hatch : hatches(machine, false)) {
                    remaining -= hatch.tank().add(type(condition), remaining, false);
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        return true;
    }

    private static List<MatterCondition> conditions(MachineRecipe recipe, boolean output) {
        List<MatterCondition> out = new ArrayList<>();
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof MatterCondition matter && matter.output() == output) {
                out.add(matter);
            }
        }
        return out;
    }
}
