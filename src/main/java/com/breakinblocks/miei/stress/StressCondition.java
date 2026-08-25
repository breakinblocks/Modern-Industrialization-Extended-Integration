package com.breakinblocks.miei.stress;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record StressCondition(double su, int rpm) implements MachineProcessCondition {
    public static final MapCodec<StressCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.DOUBLE.fieldOf("su").forGetter(StressCondition::su),
        Codec.INT.optionalFieldOf("rpm", 0).forGetter(StressCondition::rpm)
    ).apply(instance, StressCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StressCondition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, StressCondition::su,
        ByteBufCodecs.VAR_INT, StressCondition::rpm,
        StressCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return StressCrafting.canRun(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        String shown = su == Math.floor(su) ? String.valueOf((long) su) : String.format("%.2f", su);
        list.add(Component.translatable("miei.condition.stress", shown));
        if (rpm > 0) {
            list.add(Component.translatable("miei.condition.stress_rpm", rpm));
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
