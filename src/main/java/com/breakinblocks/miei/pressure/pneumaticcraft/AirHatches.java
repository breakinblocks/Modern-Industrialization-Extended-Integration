package com.breakinblocks.miei.pressure.pneumaticcraft;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.MIEI;

import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.pressure.PressureTier;

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

public final class AirHatches {
    public static final int VOLUME = 10000;
    public static final String[] TIERS = {"ev", "iv", "superconductor"};
    public static final String[] TIER_NAMES = {"EV", "IV", "SV"};
    public static final PressureTier[] PRESSURE_TIERS = {PressureTier.TIER_ONE, PressureTier.TIER_ONE_HALF, PressureTier.TIER_TWO};

    public static final HatchType TYPE = HatchTypes.register(MIEI.id("air_input"),
        Component.translatable("miei.hatch_type.air_input"));

    private static final List<MachineDefinition<AirHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private AirHatches() {
    }

    public static void register() {
        for (int i = 0; i < TIERS.length; i++) {
            String tier = TIERS[i];
            String id = tier + "_air_input_hatch";
            PressureTier pressureTier = PRESSURE_TIERS[i];
            DEFINITIONS.add(MachineRegistrationHelper.registerMachine(TIER_NAMES[i] + " Air Input Hatch", id,
                bep -> new AirHatchBlockEntity(bep, id, pressureTier, VOLUME)));
            MachineModelsToGenerate.register(id, new MachineModelProperties.Builder(MachineCasings.get(tier))
                .addOverlay("side", MI.id("block/machines/hatch_air/overlay_side"))
                .build());
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<AirHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(PNCCapabilities.AIR_HANDLER_MACHINE, definition.blockEntityType().get(),
                (hatch, side) -> hatch.tank().handler());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<AirHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
