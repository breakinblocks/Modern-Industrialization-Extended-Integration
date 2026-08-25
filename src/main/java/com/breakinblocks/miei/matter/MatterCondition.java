package com.breakinblocks.miei.matter;

import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.matter.replication.MatterCrafting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MatterCondition(String matter, double amount, boolean output) implements MachineProcessCondition {
    public static final MapCodec<MatterCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("matter").forGetter(MatterCondition::matter),
        Codec.DOUBLE.fieldOf("amount").forGetter(MatterCondition::amount),
        Codec.BOOL.optionalFieldOf("output", false).forGetter(MatterCondition::output)
    ).apply(instance, MatterCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MatterCondition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, MatterCondition::matter,
        ByteBufCodecs.DOUBLE, MatterCondition::amount,
        ByteBufCodecs.BOOL, MatterCondition::output,
        MatterCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.REPLICATION && MatterCrafting.canProcess(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        String shown = amount == Math.floor(amount) ? String.valueOf((long) amount) : String.format("%.2f", amount);
        list.add(Component.translatable(output ? "miei.condition.matter_output" : "miei.condition.matter_input",
            shown, Component.translatable("miei.matter." + matter.toLowerCase())));
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
