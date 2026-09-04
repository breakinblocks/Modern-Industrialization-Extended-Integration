package com.breakinblocks.miei.chronon.tempad;

import java.util.ArrayList;
import java.util.List;

import com.breakinblocks.miei.MIEI;

import earth.terrarium.tempad.api.tva_device.ChrononHandler;

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

public final class ChrononHatches {
    public static final int CAPACITY = 10000;
    public static final int RATE = 500;
    public static final String CASING = "ev";
    public static final String ID = "chronon_input_hatch";

    public static final HatchType TYPE = HatchTypes.register(MIEI.id("chronon_input"),
        Component.translatable("miei.hatch_type.chronon_input"));

    private static final List<MachineDefinition<ChrononHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private ChrononHatches() {
    }

    public static void register() {
        DEFINITIONS.add(MachineRegistrationHelper.registerMachine("Chronon Input Hatch", ID,
            bep -> new ChrononHatchBlockEntity(bep, ID, CAPACITY, RATE)));
        MachineModelsToGenerate.register(ID, new MachineModelProperties.Builder(MachineCasings.get(CASING))
            .addOverlay("side", MI.id("block/machines/hatch_chronon/overlay_side"))
            .build());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<ChrononHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(ChrononHandler.Capabilities.getBlock(), definition.blockEntityType().get(),
                (hatch, side) -> hatch.buffer());
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<ChrononHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
