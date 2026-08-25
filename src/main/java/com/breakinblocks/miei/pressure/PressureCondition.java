package com.breakinblocks.miei.pressure;

import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.pressure.pneumaticcraft.PressureCrafting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PressureCondition(int air, float pressure) implements MachineProcessCondition {
    public static final MapCodec<PressureCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("air").forGetter(PressureCondition::air),
        Codec.FLOAT.optionalFieldOf("pressure", 0f).forGetter(PressureCondition::pressure)
    ).apply(instance, PressureCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PressureCondition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, PressureCondition::air,
        ByteBufCodecs.FLOAT, PressureCondition::pressure,
        PressureCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.PNEUMATIC && PressureCrafting.canProcess(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.pressure", air));
        if (pressure > 0) {
            String shown = pressure == Math.floor(pressure) ? String.valueOf((long) pressure) : String.format("%.1f", pressure);
            list.add(Component.translatable("miei.condition.pressure_min", shown));
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
