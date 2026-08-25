package com.breakinblocks.miei.stress;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.MIEI;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.MachineModelProperties;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.init.MachineDefinition;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class StressHatches {
    public static final String[] TIERS = {"ev", "iv", "superconductor"};
    public static final String[] TIER_NAMES = {"EV", "IV", "SV"};

    public static final HatchType TYPE = HatchTypes.register(MIEI.id("stress_input"),
        Component.translatable("miei.hatch_type.stress_input"));

    private static final List<MachineDefinition<StressHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private StressHatches() {
    }

    public static void register() {
        for (int i = 0; i < TIERS.length; i++) {
            String tier = TIERS[i];
            String id = tier + "_stress_input_hatch";
            DEFINITIONS.add(MachineRegistrationHelper.registerMachine(TIER_NAMES[i] + " Stress Input Hatch", id,
                bep -> new StressHatchBlockEntity(bep, id)));
            MachineModelsToGenerate.register(id, new MachineModelProperties.Builder(MachineCasings.get(tier))
                .addOverlay("side", MI.id("block/machines/hatch_stress/overlay_side"))
                .build());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<StressHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
