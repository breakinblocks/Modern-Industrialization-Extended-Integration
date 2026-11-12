package com.breakinblocks.miei.particle;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.MIEI;
import com.breakinblocks.miei.compat.ConditionIcons;
import com.breakinblocks.miei.particle.nuclearcraft.ParticleCrafting;
import com.breakinblocks.miei.particle.nuclearcraft.ParticleHatches;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ParticleCondition(ResourceLocation particle, long minEnergy, double minFocus, long minAmount)
        implements MachineProcessCondition {
    public static final MapCodec<ParticleCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("particle").forGetter(ParticleCondition::particle),
                    Codec.LONG.optionalFieldOf("min_energy", 0L).forGetter(ParticleCondition::minEnergy),
                    Codec.doubleRange(0, Double.MAX_VALUE)
                            .optionalFieldOf("min_focus", 0.0)
                            .forGetter(ParticleCondition::minFocus),
                    Codec.LONG.optionalFieldOf("min_amount", 0L).forGetter(ParticleCondition::minAmount))
            .apply(instance, ParticleCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleCondition> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ParticleCondition::particle,
            ByteBufCodecs.VAR_LONG,
            ParticleCondition::minEnergy,
            ByteBufCodecs.DOUBLE,
            ParticleCondition::minFocus,
            ByteBufCodecs.VAR_LONG,
            ParticleCondition::minAmount,
            ParticleCondition::new);

    public boolean accepts(ResourceLocation id, long energy, double focus) {
        return particle.equals(id) && energy >= minEnergy && focus >= minFocus;
    }

    public static String name(ResourceLocation id) {
        String path = id.getPath().replace('_', ' ');
        return path.isEmpty() ? path : path.substring(0, 1).toUpperCase(Locale.ROOT) + path.substring(1);
    }

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return MIEI.NUCLEARCRAFT && ParticleCrafting.canRun(context.getBlockEntity(), this);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.add(Component.translatable("miei.condition.particle", name(particle)));
        if (minEnergy > 0) {
            list.add(Component.translatable(
                    "miei.condition.particle_energy", String.format(Locale.ROOT, "%,d", minEnergy)));
        }
        if (minFocus > 0) {
            list.add(Component.translatable(
                    "miei.condition.particle_focus", String.format(Locale.ROOT, "%.2f", minFocus)));
        }
        if (minAmount > 0) {
            list.add(Component.translatable(
                    "miei.condition.particle_amount", String.format(Locale.ROOT, "%,d", minAmount)));
        }
    }

    @Override
    public ItemStack icon() {
        return MIEI.NUCLEARCRAFT ? ConditionIcons.hatch(ParticleHatches.stacks(), false) : ItemStack.EMPTY;
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
