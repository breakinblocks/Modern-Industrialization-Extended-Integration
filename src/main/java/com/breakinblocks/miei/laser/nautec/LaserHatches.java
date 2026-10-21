package com.breakinblocks.miei.laser.nautec;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.MachineModelProperties;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.init.MachineDefinition;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.nautec.capabilities.NTCapabilities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class LaserHatches {
    public static final String CASING = "ev";
    public static final String ID = "nautechnical_hatch";

    public static final HatchType TYPE =
            HatchTypes.register(MIEI.id("laser_input"), Component.translatable("miei.hatch_type.laser_input"));

    private static final List<MachineDefinition<LaserHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private LaserHatches() {}

    public static void register() {
        DEFINITIONS.add(MachineRegistrationHelper.registerMachine(
                "Nautechnical Hatch", ID, bep -> new LaserHatchBlockEntity(bep, ID)));
        MachineModelsToGenerate.register(
                ID,
                new MachineModelProperties.Builder(MachineCasings.get(CASING))
                        .addOverlay("side", MI.id("block/machines/hatch_laser/overlay_side"))
                        .build());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<LaserHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(
                    NTCapabilities.BeamReceiver.BLOCK,
                    definition.blockEntityType().get(),
                    (hatch, side) -> hatch);
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<LaserHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
