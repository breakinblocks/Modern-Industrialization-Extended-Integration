package com.breakinblocks.miei.heat;

import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.heat.pneumaticcraft.HeatCrafting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HeatCondition(int min, int max, double heat) implements MachineProcessCondition {
    public static final int NO_MAX = Integer.MAX_VALUE;

    public static final MapCodec<HeatCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.optionalFieldOf("min", 0).forGetter(HeatCondition::min),
        Codec.INT.optionalFieldOf("max", NO_MAX).forGetter(HeatCondition::max),
        Codec.DOUBLE.optionalFieldOf("heat", 0d).forGetter(HeatCondition::heat)
    ).apply(instance, HeatCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HeatCondition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, HeatCondition::min,
        ByteBufCodecs.VAR_INT, HeatCondition::max,
        ByteBufCodecs.DOUBLE, HeatCondition::heat,
        HeatCondition::new);

    public boolean inRange(double temperature) {
        return temperature >= min && temperature <= max;
    }

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.PNEUMATIC && HeatCrafting.canProcess(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        if (min > 0 && max < NO_MAX) {
            list.add(Component.translatable("miei.condition.heat_range", min, max));
        } else if (min > 0) {
            list.add(Component.translatable("miei.condition.heat_min", min));
        } else if (max < NO_MAX) {
            list.add(Component.translatable("miei.condition.heat_max", max));
        }
        if (heat > 0) {
            String shown = heat == Math.floor(heat) ? String.valueOf((long) heat) : String.format("%.1f", heat);
            list.add(Component.translatable("miei.condition.heat_draw", shown));
        }
    }

    @Override
    public MapCodec<? extends MachineProcessCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec() {
        return STREAM_CODEC;
    }
}
