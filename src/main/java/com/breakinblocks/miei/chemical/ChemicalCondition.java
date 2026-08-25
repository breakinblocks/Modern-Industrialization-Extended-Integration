package com.breakinblocks.miei.chemical;

import java.util.List;

import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.chemical.mekanism.ChemicalCrafting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ChemicalCondition(ResourceLocation chemical, long amount, boolean output) implements MachineProcessCondition {
    public static final MapCodec<ChemicalCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("chemical").forGetter(ChemicalCondition::chemical),
        Codec.LONG.fieldOf("amount").forGetter(ChemicalCondition::amount),
        Codec.BOOL.optionalFieldOf("output", false).forGetter(ChemicalCondition::output)
    ).apply(instance, ChemicalCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalCondition> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, ChemicalCondition::chemical,
        ByteBufCodecs.VAR_LONG, ChemicalCondition::amount,
        ByteBufCodecs.BOOL, ChemicalCondition::output,
        ChemicalCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.MEKANISM && ChemicalCrafting.canProcess(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable(output ? "miei.condition.chemical_output" : "miei.condition.chemical_input",
            amount, Component.translatable("chemical." + chemical.getNamespace() + "." + chemical.getPath())));
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
