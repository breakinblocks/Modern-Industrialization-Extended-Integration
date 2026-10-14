package com.breakinblocks.miei.source.arsnouveau;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.MachineModelProperties;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.init.MachineDefinition;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import com.breakinblocks.miei.MIEI;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class SourceHatches {
    public static final String[] TIERS = {"lv", "mv", "hv", "ev", "iv", "superconductor"};
    public static final String[] TIER_NAMES = {"LV", "MV", "HV", "EV", "IV", "SV"};
    public static final int[] CAPACITIES = {10000, 25000, 50000, 100000, 250000, 1000000};

    public static final HatchType TYPE =
            HatchTypes.register(MIEI.id("source_input"), Component.translatable("miei.hatch_type.source_input"));

    private static final List<MachineDefinition<SourceHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private SourceHatches() {}

    public static void register() {
        for (int i = 0; i < TIERS.length; i++) {
            String tier = TIERS[i];
            if (!MIEI.hasCasing(tier)) {
                continue;
            }
            String id = tier + "_source_input_hatch";
            int capacity = CAPACITIES[i];
            DEFINITIONS.add(MachineRegistrationHelper.registerMachine(
                    TIER_NAMES[i] + " Source Input Hatch", id, bep -> new SourceHatchBlockEntity(bep, id, capacity)));
            MachineModelsToGenerate.register(
                    id,
                    new MachineModelProperties.Builder(MachineCasings.get(tier))
                            .addOverlay("side", MI.id("block/machines/hatch_source/overlay_side"))
                            .build());
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<SourceHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(
                    CapabilityRegistry.SOURCE_CAPABILITY,
                    definition.blockEntityType().get(),
                    (hatch, side) -> hatch.buffer());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<SourceHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
