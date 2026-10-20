package com.breakinblocks.miei.stage;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record StageCondition(String stage) implements MachineProcessCondition {
    public static final MapCodec<StageCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codec.STRING.fieldOf("stage").forGetter(StageCondition::stage))
                    .apply(instance, StageCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StageCondition> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, StageCondition::stage, StageCondition::new);

    public static Component name(String stage) {
        return Component.translatableWithFallback("miei.stage." + stage, stage);
    }

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        MachineBlockEntity machine = context.getBlockEntity();
        ServerLevel level = context.getLevel();
        boolean met = PlayerStages.has(level, machine.placedBy.placerId, stage);
        WaitingStages.update(machine, stage, met, level.getGameTime());
        return met;
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.stage", name(stage)));
    }

    @Override
    public ItemStack icon() {
        return new ItemStack(Items.KNOWLEDGE_BOOK);
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
