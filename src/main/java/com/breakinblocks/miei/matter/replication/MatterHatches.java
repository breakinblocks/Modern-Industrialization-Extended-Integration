package com.breakinblocks.miei.matter.replication;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.buuz135.replication.ReplicationRegistry;

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

public final class MatterHatches {
    public static final double CAPACITY = 8000;
    public static final String[] TIERS = {"ev", "iv", "superconductor"};
    public static final String[] TIER_NAMES = {"EV", "IV", "SV"};

    public static final HatchType INPUT = HatchTypes.register(MIEI.id("matter_input"), Component.translatable("miei.hatch_type.matter_input"));
    public static final HatchType OUTPUT = HatchTypes.register(MIEI.id("matter_output"), Component.translatable("miei.hatch_type.matter_output"));

    private static final List<MachineDefinition<MatterHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private MatterHatches() {
    }

    public static void register() {
        for (int i = 0; i < TIERS.length; i++) {
            String tier = TIERS[i];
            DEFINITIONS.add(registerHatch(tier, TIER_NAMES[i] + " Matter Input Hatch", tier + "_matter_input_hatch", true));
            DEFINITIONS.add(registerHatch(tier, TIER_NAMES[i] + " Matter Output Hatch", tier + "_matter_output_hatch", false));
        }
    }

    private static MachineDefinition<MatterHatchBlockEntity> registerHatch(String tier, String english, String id, boolean input) {
        MachineDefinition<MatterHatchBlockEntity> definition = MachineRegistrationHelper.registerMachine(
            english, id, bep -> new MatterHatchBlockEntity(bep, id, input, CAPACITY));
        MachineModelProperties.Builder model = new MachineModelProperties.Builder(MachineCasings.get(tier))
            .addOverlay("side", MI.id("block/machines/hatch_matter/overlay_side"));
        if (!input) {
            model.addOverlay("output", MI.id("block/machines/hatch_matter/output"));
        }
        MachineModelsToGenerate.register(id, model.build());
        return definition;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<MatterHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(ReplicationRegistry.Capabilities.MATTER_HANDLER, definition.blockEntityType().get(),
                (hatch, side) -> hatch.tank());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<MatterHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
