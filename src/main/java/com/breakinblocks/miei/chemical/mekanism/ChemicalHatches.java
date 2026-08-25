package com.breakinblocks.miei.chemical.mekanism;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.MIEI;

import mekanism.common.capabilities.Capabilities;

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

public final class ChemicalHatches {
    public static final String[] TIERS = {"ev", "iv", "superconductor"};
    public static final String[] TIER_NAMES = {"EV", "IV", "SV"};
    public static final long[] CAPACITIES = {64000, 256000, 1024000};

    public static final HatchType INPUT = HatchTypes.register(MIEI.id("chemical_input"),
        Component.translatable("miei.hatch_type.chemical_input"));
    public static final HatchType OUTPUT = HatchTypes.register(MIEI.id("chemical_output"),
        Component.translatable("miei.hatch_type.chemical_output"));

    private static final List<MachineDefinition<ChemicalHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private ChemicalHatches() {
    }

    public static void register() {
        for (int i = 0; i < TIERS.length; i++) {
            String tier = TIERS[i];
            long capacity = CAPACITIES[i];
            registerHatch(tier, TIER_NAMES[i] + " Chemical Input Hatch", tier + "_chemical_input_hatch", true, capacity);
            registerHatch(tier, TIER_NAMES[i] + " Chemical Output Hatch", tier + "_chemical_output_hatch", false, capacity);
        }
    }

    private static void registerHatch(String tier, String english, String id, boolean input, long capacity) {
        DEFINITIONS.add(MachineRegistrationHelper.registerMachine(english, id,
            bep -> new ChemicalHatchBlockEntity(bep, id, input, capacity)));
        MachineModelProperties.Builder model = new MachineModelProperties.Builder(MachineCasings.get(tier))
            .addOverlay("side", MI.id("block/machines/hatch_chemical/overlay_side"));
        if (!input) {
            model.addOverlay("output", MI.id("block/machines/hatch_matter/output"));
        }
        MachineModelsToGenerate.register(id, model.build());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<ChemicalHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(Capabilities.CHEMICAL.block(), definition.blockEntityType().get(),
                (hatch, side) -> hatch.tank());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<ChemicalHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
