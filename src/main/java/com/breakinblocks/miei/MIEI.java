package com.breakinblocks.miei;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions;
import com.breakinblocks.miei.chemical.ChemicalCondition;
import com.breakinblocks.miei.chemical.mekanism.ChemicalHatches;
import com.breakinblocks.miei.chronon.ChrononCondition;
import com.breakinblocks.miei.chronon.tempad.ChrononHatches;
import com.breakinblocks.miei.chronon.tempad.ChrononTransfer;
import com.breakinblocks.miei.energy.NoEnergyCondition;
import com.breakinblocks.miei.heat.HeatCondition;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatHatches;
import com.breakinblocks.miei.laser.LaserCondition;
import com.breakinblocks.miei.laser.nautec.LaserHatches;
import com.breakinblocks.miei.matter.MatterCondition;
import com.breakinblocks.miei.matter.replication.MatterHatches;
import com.breakinblocks.miei.pressure.PressureCondition;
import com.breakinblocks.miei.pressure.pneumaticcraft.AirHatches;
import com.breakinblocks.miei.source.SourceCondition;
import com.breakinblocks.miei.source.arsnouveau.SourceHatches;
import com.breakinblocks.miei.stage.PlayerStages;
import com.breakinblocks.miei.stage.StageCondition;
import com.breakinblocks.miei.stress.StressCondition;
import com.breakinblocks.miei.stress.create.CreateSupport;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(MIEI.MOD_ID)
public final class MIEI {
    public static final String MOD_ID = "miei";

    public static final boolean REPLICATION = ModList.get().isLoaded("replication");
    public static final boolean CREATE = ModList.get().isLoaded("create");
    public static final boolean PNEUMATIC = ModList.get().isLoaded("pneumaticcraft");
    public static final boolean MEKANISM = ModList.get().isLoaded("mekanism");
    public static final boolean TEMPAD = ModList.get().isLoaded("tempad");
    public static final boolean ARS_NOUVEAU = ModList.get().isLoaded("ars_nouveau");
    public static final boolean NAUTEC = ModList.get().isLoaded("nautec");

    public MIEI(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, MIEIConfig.SPEC);
        MachineProcessConditions.register(id("matter"), MatterCondition.CODEC, MatterCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("stress"), StressCondition.CODEC, StressCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("pressure"), PressureCondition.CODEC, PressureCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("heat"), HeatCondition.CODEC, HeatCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("chemical"), ChemicalCondition.CODEC, ChemicalCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("chronon"), ChrononCondition.CODEC, ChrononCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("source"), SourceCondition.CODEC, SourceCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("laser"), LaserCondition.CODEC, LaserCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("no_energy"), NoEnergyCondition.CODEC, NoEnergyCondition.STREAM_CODEC);
        MachineProcessConditions.register(id("stage"), StageCondition.CODEC, StageCondition.STREAM_CODEC);
        PlayerStages.register(NeoForge.EVENT_BUS);
        if (REPLICATION) {
            modBus.addListener((RegisterCapabilitiesEvent event) -> MatterHatches.registerCapabilities(event));
        }
        if (CREATE) {
            CreateSupport.init(modBus);
        }
        if (PNEUMATIC) {
            modBus.addListener((RegisterCapabilitiesEvent event) -> {
                AirHatches.registerCapabilities(event);
                HeatHatches.registerCapabilities(event);
            });
        }
        if (MEKANISM) {
            modBus.addListener((RegisterCapabilitiesEvent event) -> ChemicalHatches.registerCapabilities(event));
        }
        if (TEMPAD) {
            modBus.addListener((RegisterCapabilitiesEvent event) -> ChrononHatches.registerCapabilities(event));
            ChrononTransfer.register(NeoForge.EVENT_BUS);
        }
        if (ARS_NOUVEAU) {
            modBus.addListener((RegisterCapabilitiesEvent event) -> SourceHatches.registerCapabilities(event));
        }
        if (NAUTEC) {
            modBus.addListener((RegisterCapabilitiesEvent event) -> LaserHatches.registerCapabilities(event));
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static boolean hasCasing(String name) {
        return MachineCasings.registeredCasings.containsKey(MI.id(name));
    }
}
