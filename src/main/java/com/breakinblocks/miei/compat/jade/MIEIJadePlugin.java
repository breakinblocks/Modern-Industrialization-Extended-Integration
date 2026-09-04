package com.breakinblocks.miei.compat.jade;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chemical.mekanism.ChemicalHatchBlockEntity;
import com.breakinblocks.miei.chronon.tempad.ChrononHatchBlockEntity;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatHatchBlockEntity;
import com.breakinblocks.miei.matter.replication.MatterHatchBlockEntity;
import com.breakinblocks.miei.pressure.pneumaticcraft.AirHatchBlockEntity;
import com.breakinblocks.miei.stress.StressHatchBlockEntity;

import aztech.modern_industrialization.machines.MachineBlock;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(MIEI.MOD_ID)
public final class MIEIJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        if (MIEI.REPLICATION) {
            registration.registerBlockDataProvider(
                new HatchDataProvider(MIEI.id("matter_hatch")), MatterHatchBlockEntity.class);
        }
        if (MIEI.CREATE) {
            registration.registerBlockDataProvider(
                new HatchDataProvider(MIEI.id("stress_hatch")), StressHatchBlockEntity.class);
        }
        if (MIEI.PNEUMATIC) {
            registration.registerBlockDataProvider(
                new HatchDataProvider(MIEI.id("air_hatch")), AirHatchBlockEntity.class);
            registration.registerBlockDataProvider(
                new HatchDataProvider(MIEI.id("heat_hatch")), HeatHatchBlockEntity.class);
        }
        if (MIEI.MEKANISM) {
            registration.registerBlockDataProvider(
                new HatchDataProvider(MIEI.id("chemical_hatch")), ChemicalHatchBlockEntity.class);
        }
        if (MIEI.TEMPAD) {
            registration.registerBlockDataProvider(
                new HatchDataProvider(MIEI.id("chronon_hatch")), ChrononHatchBlockEntity.class);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(HatchTooltipProvider.INSTANCE, MachineBlock.class);
    }
}
