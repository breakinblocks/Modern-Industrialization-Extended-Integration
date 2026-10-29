package com.breakinblocks.miei.laser;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.compat.ConditionIcons;
import com.breakinblocks.miei.laser.nautec.LaserCrafting;
import com.breakinblocks.miei.laser.nautec.LaserHatches;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record LaserCondition(int power, float purity) implements MachineProcessCondition {
    public static final MapCodec<LaserCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, Integer.MAX_VALUE).fieldOf("power").forGetter(LaserCondition::power),
                    Codec.floatRange(0f, 1f).optionalFieldOf("purity", 0f).forGetter(LaserCondition::purity))
            .apply(instance, LaserCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            LaserCondition::power,
            ByteBufCodecs.FLOAT,
            LaserCondition::purity,
            LaserCondition::new);

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.NAUTEC && LaserCrafting.canRun(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.laser", String.format(Locale.ROOT, "%,d", power)));
        if (purity > 0) {
            list.add(Component.translatable("miei.condition.laser_purity", String.format(Locale.ROOT, "%.2f", purity)));
        }
    }

    @Override
    public ItemStack icon() {
        return MIEI.NAUTEC ? ConditionIcons.hatch(LaserHatches.stacks(), false) : ItemStack.EMPTY;
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
