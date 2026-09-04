package com.breakinblocks.miei.chronon;

import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chronon.tempad.ChrononCrafting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ChrononCondition(int amount) implements MachineProcessCondition {
    public static final MapCodec<ChrononCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("amount").forGetter(ChrononCondition::amount)
    ).apply(instance, ChrononCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChrononCondition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ChrononCondition::amount,
        ChrononCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.TEMPAD && ChrononCrafting.canProcess(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.chronon", amount));
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
