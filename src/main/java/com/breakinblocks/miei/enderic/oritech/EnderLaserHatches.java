package com.breakinblocks.miei.enderic.oritech;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.MachineModelProperties;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.init.MachineDefinition;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import com.breakinblocks.miei.MIEI;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class EnderLaserHatches {
    public static final String CASING = "ev";
    public static final String ID = "enderic_laser_hatch";

    public static final HatchType TYPE = HatchTypes.register(
            MIEI.id("enderic_laser_input"), Component.translatable("miei.hatch_type.enderic_laser_input"));

    private static final List<MachineDefinition<EnderLaserHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private EnderLaserHatches() {}

    public static void register() {
        DEFINITIONS.add(MachineRegistrationHelper.registerMachine(
                "Enderic Laser Hatch", ID, bep -> new EnderLaserHatchBlockEntity(bep, ID)));
        MachineModelsToGenerate.register(
                ID,
                new MachineModelProperties.Builder(MachineCasings.get(CASING))
                        .addOverlay("side", MI.id("block/machines/hatch_enderic_laser/overlay_side"))
                        .build());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<EnderLaserHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(
                    ILongEnergyStorage.BLOCK,
                    definition.blockEntityType().get(),
                    (hatch, side) -> side == null ? hatch.receiver() : null);
        }
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<EnderLaserHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
