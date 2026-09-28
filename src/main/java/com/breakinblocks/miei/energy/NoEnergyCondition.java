package com.breakinblocks.miei.energy;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public record NoEnergyCondition() implements MachineProcessCondition {
    public static final NoEnergyCondition INSTANCE = new NoEnergyCondition();

    public static final MapCodec<NoEnergyCondition> CODEC = MapCodec.unit(INSTANCE);

    public static final StreamCodec<RegistryFriendlyByteBuf, NoEnergyCondition> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return true;
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.no_energy"));
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
