package com.breakinblocks.miei.particle.nuclearcraft;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.MachineModelProperties;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.init.MachineDefinition;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import com.breakinblocks.miei.MIEI;
import igentuman.nc.api.particle.ParticleCapabilities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class ParticleHatches {
    public static final String CASING = "ev";
    public static final String ID = "particle_beam_hatch";

    public static final HatchType TYPE =
            HatchTypes.register(MIEI.id("particle_input"), Component.translatable("miei.hatch_type.particle_input"));

    private static final List<MachineDefinition<ParticleHatchBlockEntity>> DEFINITIONS = new ArrayList<>();

    private ParticleHatches() {}

    public static void register() {
        DEFINITIONS.add(MachineRegistrationHelper.registerMachine(
                "Particle Beam Hatch", ID, bep -> new ParticleHatchBlockEntity(bep, ID)));
        MachineModelsToGenerate.register(
                ID,
                new MachineModelProperties.Builder(MachineCasings.get(CASING))
                        .addOverlay("side", MI.id("block/machines/hatch_particle/overlay_side"))
                        .build());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (MachineDefinition<ParticleHatchBlockEntity> definition : DEFINITIONS) {
            event.registerBlockEntity(
                    ParticleCapabilities.BLOCK, definition.blockEntityType().get(), (hatch, side) -> hatch);
        }
    }

    public static boolean isHatch(Block block) {
        for (MachineDefinition<ParticleHatchBlockEntity> definition : DEFINITIONS) {
            if (definition.asBlock() == block) {
                return true;
            }
        }
        return false;
    }

    public static List<ItemStack> stacks() {
        List<ItemStack> out = new ArrayList<>();
        for (MachineDefinition<ParticleHatchBlockEntity> definition : DEFINITIONS) {
            out.add(new ItemStack(definition.asBlock()));
        }
        return out;
    }
}
