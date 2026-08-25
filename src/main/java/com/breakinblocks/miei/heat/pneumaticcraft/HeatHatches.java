package com.breakinblocks.miei.heat.pneumaticcraft;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.MIEI;

import me.desht.pneumaticcraft.api.PNCCapabilities;

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

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class HeatHatches {
    public static final String[] TIERS = {"ev", "iv", "superconductor"};
    public static final String[] TIER_NAMES = {"EV", "IV", "SV"};
    public static final double[] THERMAL_CAPACITIES = {10, 25, 50};

    public static final HatchType TYPE = HatchTypes.register(MIEI.id("heat"),
        Component.translatable("miei.hatch_type.heat"));

    private static final List<MachineDefinition<HeatHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private HeatHatches() {
    }

    public static void register() {
        for (int i = 0; i < TIERS.length; i++) {
            String tier = TIERS[i];
            String id = tier + "_heat_hatch";
            double capacity = THERMAL_CAPACITIES[i];
            DEFINITIONS.add(MachineRegistrationHelper.registerMachine(TIER_NAMES[i] + " Heat Hatch", id,
                bep -> new HeatHatchBlockEntity(bep, id, capacity)));
            MachineModelsToGenerate.register(id, new MachineModelProperties.Builder(MachineCasings.get(tier))
                .addOverlay("side", MI.id("block/machines/hatch_heat/overlay_side"))
                .build());
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<HeatHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(PNCCapabilities.HEAT_EXCHANGER_BLOCK, definition.blockEntityType().get(),
                (hatch, side) -> hatch.exchanger().logic());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<HeatHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
