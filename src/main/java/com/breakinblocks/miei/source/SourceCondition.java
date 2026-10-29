package com.breakinblocks.miei.source;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.RunningCheck;
import com.breakinblocks.miei.compat.ConditionIcons;
import com.breakinblocks.miei.source.arsnouveau.SourceCrafting;
import com.breakinblocks.miei.source.arsnouveau.SourceHatches;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SourceCondition(int amount) implements MachineProcessCondition {
    public static final MapCodec<SourceCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codec.INT.fieldOf("amount").forGetter(SourceCondition::amount))
                    .apply(instance, SourceCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SourceCondition> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SourceCondition::amount, SourceCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.ARS_NOUVEAU && (RunningCheck.active() || SourceCrafting.canProcess(context.getBlockEntity(), this));
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.source", amount));
    }

    @Override
    public ItemStack icon() {
        return MIEI.ARS_NOUVEAU ? ConditionIcons.hatch(SourceHatches.stacks(), false) : ItemStack.EMPTY;
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
