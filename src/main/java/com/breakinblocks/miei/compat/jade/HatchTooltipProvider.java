package com.breakinblocks.miei.compat.jade;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.compat.HatchKeys;
import com.breakinblocks.miei.stage.StageCondition;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class HatchTooltipProvider implements IBlockComponentProvider {
    public static final HatchTooltipProvider INSTANCE = new HatchTooltipProvider();
    public static final ResourceLocation UID = MIEI.id("hatch");

    private HatchTooltipProvider() {}

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null) {
            return;
        }
        if (data.contains(HatchKeys.MATTER_LIST)) {
            double capacity = data.getDouble(HatchKeys.MATTER_CAPACITY);
            for (Tag entry : data.getList(HatchKeys.MATTER_LIST, Tag.TAG_COMPOUND)) {
                CompoundTag matter = (CompoundTag) entry;
                tooltip.add(Component.translatable(
                        "miei.jade.matter",
                        Component.translatable(matter.getString(HatchKeys.MATTER_TYPE)),
                        amount(matter.getDouble(HatchKeys.MATTER_AMOUNT)),
                        amount(capacity)));
            }
        } else if (data.contains(HatchKeys.MATTER_EMPTY)) {
            tooltip.add(Component.translatable("miei.jade.empty"));
        }
        if (data.contains(HatchKeys.STRESS_LINKED)) {
            if (data.getBoolean(HatchKeys.STRESS_LINKED)) {
                tooltip.add(Component.translatable(
                        "miei.jade.stress_speed", amount(Math.abs(data.getFloat(HatchKeys.STRESS_SPEED)))));
                tooltip.add(Component.translatable(
                        "miei.jade.stress_demand", amount(data.getDouble(HatchKeys.STRESS_DEMAND))));
            } else {
                tooltip.add(Component.translatable("miei.jade.stress_unlinked"));
            }
        }
        if (data.contains(HatchKeys.AIR)) {
            tooltip.add(Component.translatable("miei.jade.air", data.getInt(HatchKeys.AIR)));
            tooltip.add(Component.translatable(
                    "miei.jade.pressure",
                    String.format("%.2f", data.getFloat(HatchKeys.PRESSURE)),
                    String.format("%.2f", data.getFloat(HatchKeys.DANGER_PRESSURE))));
            tooltip.add(Component.translatable(
                    data.getBoolean(HatchKeys.SECURED) ? "miei.jade.secured" : "miei.jade.unsecured"));
        }
        if (data.contains(HatchKeys.TEMPERATURE)) {
            tooltip.add(Component.translatable("miei.jade.temperature", amount(data.getDouble(HatchKeys.TEMPERATURE))));
        }
        if (data.contains(HatchKeys.CHEMICAL_TYPE)) {
            tooltip.add(Component.translatable(
                    "miei.jade.chemical",
                    Component.translatable(data.getString(HatchKeys.CHEMICAL_TYPE)),
                    data.getLong(HatchKeys.CHEMICAL_AMOUNT),
                    data.getLong(HatchKeys.CHEMICAL_CAPACITY)));
        } else if (data.contains(HatchKeys.CHEMICAL_EMPTY)) {
            tooltip.add(Component.translatable("miei.jade.empty"));
        }
        if (data.contains(HatchKeys.CHRONON_AMOUNT)) {
            tooltip.add(Component.translatable(
                    "miei.jade.chronon",
                    data.getInt(HatchKeys.CHRONON_AMOUNT),
                    data.getInt(HatchKeys.CHRONON_CAPACITY)));
            if (data.contains(HatchKeys.CHRONON_SOURCE)) {
                BlockPos source = BlockPos.of(data.getLong(HatchKeys.CHRONON_SOURCE));
                tooltip.add(Component.translatable(
                        "miei.jade.chronon_source", source.getX(), source.getY(), source.getZ()));
            } else {
                tooltip.add(
                        Component.translatable("miei.jade.chronon_unlinked", data.getInt(HatchKeys.CHRONON_RADIUS)));
                tooltip.add(Component.translatable(
                        "miei.jade.chronon_next_scan",
                        String.format("%.1f", data.getInt(HatchKeys.CHRONON_NEXT_SCAN) / 20f)));
            }
            if (data.contains(HatchKeys.CHRONON_ITEM)) {
                tooltip.add(Component.translatable(
                        "miei.jade.chronon_item",
                        Component.translatable(data.getString(HatchKeys.CHRONON_ITEM)),
                        data.getInt(HatchKeys.CHRONON_ITEM_POWER),
                        data.getInt(HatchKeys.CHRONON_ITEM_CAPACITY)));
            }
        }
        if (data.contains(HatchKeys.SOURCE_AMOUNT)) {
            tooltip.add(Component.translatable(
                    "miei.jade.source", data.getInt(HatchKeys.SOURCE_AMOUNT), data.getInt(HatchKeys.SOURCE_CAPACITY)));
        }
        if (data.contains(HatchKeys.LASER_POWER)) {
            int power = data.getInt(HatchKeys.LASER_POWER);
            if (power > 0) {
                tooltip.add(Component.translatable(
                        "miei.jade.laser",
                        String.format(Locale.ROOT, "%,d", power),
                        String.format(Locale.ROOT, "%.2f", data.getFloat(HatchKeys.LASER_PURITY))));
            } else {
                tooltip.add(Component.translatable("miei.jade.laser_none").withStyle(ChatFormatting.GRAY));
            }
        }
        if (data.contains(HatchKeys.STAGE_WAITING)) {
            ListTag stages = data.getList(HatchKeys.STAGE_WAITING, Tag.TAG_STRING);
            for (int i = 0; i < stages.size(); i++) {
                tooltip.add(Component.translatable("miei.jade.stage_waiting", StageCondition.name(stages.getString(i)))
                        .withStyle(ChatFormatting.RED));
            }
            if (data.getBoolean(HatchKeys.STAGE_NO_OWNER)) {
                tooltip.add(Component.translatable("miei.jade.stage_no_owner").withStyle(ChatFormatting.RED));
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
