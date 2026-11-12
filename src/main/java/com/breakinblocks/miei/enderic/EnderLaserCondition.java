package com.breakinblocks.miei.enderic;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.compat.ConditionIcons;
import com.breakinblocks.miei.enderic.oritech.EnderLaserCrafting;
import com.breakinblocks.miei.enderic.oritech.EnderLaserHatches;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record EnderLaserCondition(long minRate) implements MachineProcessCondition {
    public static final MapCodec<EnderLaserCondition> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.LONG
                            .validate(value -> value >= 0
                                    ? DataResult.success(value)
                                    : DataResult.error(() -> "min_rate must not be negative"))
                            .optionalFieldOf("min_rate", 0L)
                            .forGetter(EnderLaserCondition::minRate))
                    .apply(instance, EnderLaserCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnderLaserCondition> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_LONG, EnderLaserCondition::minRate, EnderLaserCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.ORITECH && EnderLaserCrafting.canRun(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.enderic_laser"));
        if (minRate > 0) {
            list.add(Component.translatable(
                    "miei.condition.enderic_laser_rate", String.format(Locale.ROOT, "%,d", minRate)));
        }
    }

    @Override
    public ItemStack icon() {
        return MIEI.ORITECH ? ConditionIcons.hatch(EnderLaserHatches.stacks(), false) : ItemStack.EMPTY;
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
