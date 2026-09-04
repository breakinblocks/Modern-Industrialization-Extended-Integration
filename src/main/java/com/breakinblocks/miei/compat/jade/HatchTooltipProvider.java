package com.breakinblocks.miei.compat.jade;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.compat.HatchKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class HatchTooltipProvider implements IBlockComponentProvider {
    public static final HatchTooltipProvider INSTANCE = new HatchTooltipProvider();
    public static final ResourceLocation UID = MIEI.id("hatch");

    private HatchTooltipProvider() {
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null) {
            return;
        }
        if (data.contains(HatchKeys.MATTER_TYPE)) {
            tooltip.add(Component.translatable("miei.jade.matter",
                Component.translatable(data.getString(HatchKeys.MATTER_TYPE)),
                amount(data.getDouble(HatchKeys.MATTER_AMOUNT)),
                amount(data.getDouble(HatchKeys.MATTER_CAPACITY))));
        } else if (data.contains(HatchKeys.MATTER_EMPTY)) {
            tooltip.add(Component.translatable("miei.jade.empty"));
        }
        if (data.contains(HatchKeys.STRESS_LINKED)) {
            if (data.getBoolean(HatchKeys.STRESS_LINKED)) {
                tooltip.add(Component.translatable("miei.jade.stress_speed",
                    amount(Math.abs(data.getFloat(HatchKeys.STRESS_SPEED)))));
                tooltip.add(Component.translatable("miei.jade.stress_demand",
                    amount(data.getDouble(HatchKeys.STRESS_DEMAND))));
            } else {
                tooltip.add(Component.translatable("miei.jade.stress_unlinked"));
            }
        }
        if (data.contains(HatchKeys.AIR)) {
            tooltip.add(Component.translatable("miei.jade.air", data.getInt(HatchKeys.AIR)));
            tooltip.add(Component.translatable("miei.jade.pressure",
                String.format("%.2f", data.getFloat(HatchKeys.PRESSURE)),
                String.format("%.2f", data.getFloat(HatchKeys.DANGER_PRESSURE))));
            tooltip.add(Component.translatable(
                data.getBoolean(HatchKeys.SECURED) ? "miei.jade.secured" : "miei.jade.unsecured"));
        }
        if (data.contains(HatchKeys.TEMPERATURE)) {
            tooltip.add(Component.translatable("miei.jade.temperature",
                amount(data.getDouble(HatchKeys.TEMPERATURE))));
        }
        if (data.contains(HatchKeys.CHEMICAL_TYPE)) {
            tooltip.add(Component.translatable("miei.jade.chemical",
                Component.translatable(data.getString(HatchKeys.CHEMICAL_TYPE)),
                data.getLong(HatchKeys.CHEMICAL_AMOUNT),
                data.getLong(HatchKeys.CHEMICAL_CAPACITY)));
        } else if (data.contains(HatchKeys.CHEMICAL_EMPTY)) {
            tooltip.add(Component.translatable("miei.jade.empty"));
        }
        if (data.contains(HatchKeys.CHRONON_AMOUNT)) {
            tooltip.add(Component.translatable("miei.jade.chronon",
                data.getInt(HatchKeys.CHRONON_AMOUNT), data.getInt(HatchKeys.CHRONON_CAPACITY)));
            if (data.contains(HatchKeys.CHRONON_SOURCE)) {
                BlockPos source = BlockPos.of(data.getLong(HatchKeys.CHRONON_SOURCE));
                tooltip.add(Component.translatable("miei.jade.chronon_source",
                    source.getX(), source.getY(), source.getZ()));
            } else {
                tooltip.add(Component.translatable("miei.jade.chronon_unlinked", data.getInt(HatchKeys.CHRONON_RADIUS)));
                tooltip.add(Component.translatable("miei.jade.chronon_next_scan",
                    String.format("%.1f", data.getInt(HatchKeys.CHRONON_NEXT_SCAN) / 20f)));
            }
        }
    }

    private static String amount(double value) {
        return value == Math.floor(value) ? String.valueOf((long) value) : String.format("%.2f", value);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
