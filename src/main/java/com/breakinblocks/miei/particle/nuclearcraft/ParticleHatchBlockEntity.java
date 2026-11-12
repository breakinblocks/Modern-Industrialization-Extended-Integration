package com.breakinblocks.miei.particle.nuclearcraft;

import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import com.breakinblocks.miei.compat.HatchInfo;
import com.breakinblocks.miei.compat.HatchKeys;
import igentuman.nc.api.particle.IParticleHandler;
import igentuman.nc.api.particle.ParticleAction;
import igentuman.nc.api.particle.ParticleStack;
import net.minecraft.nbt.CompoundTag;

public final class ParticleHatchBlockEntity extends HatchBlockEntity implements HatchInfo, IParticleHandler {
    private long tick = Long.MIN_VALUE;
    private ParticleStack current = ParticleStack.EMPTY;
    private ParticleStack last = ParticleStack.EMPTY;

    public ParticleHatchBlockEntity(BEP bep, String id) {
        super(bep, new MachineGuiParameters.Builder(id, false).build(), OrientationComponent.Params.noFacingNoOutput());
    }

    @Override
    public int channels() {
        return 1;
    }

    @Override
    public ParticleStack snapshot(int channel) {
        return channel == 0 ? beam() : ParticleStack.EMPTY;
    }

    @Override
    public long capacity(int channel) {
        return channel == 0 ? Long.MAX_VALUE : 0L;
    }

    @Override
    public ParticleStack insert(int channel, ParticleStack stack, ParticleAction action) {
        if (channel != 0 || stack.isEmpty() || level == null) {
            return stack;
        }
        long now = level.getGameTime();
        boolean fresh = tick != now;
        ParticleStack base = fresh ? ParticleStack.EMPTY : current;
        if (!base.isEmpty() && !sameBeam(base, stack)) {
            return stack;
        }
        if (action == ParticleAction.EXECUTE) {
            if (fresh) {
                roll(now);
            }
            current = base.isEmpty() ? stack : add(base, stack.amount());
        }
        return ParticleStack.EMPTY;
    }

    @Override
    public ParticleStack extract(int channel, long maximum, ParticleAction action) {
        return ParticleStack.EMPTY;
    }

    private static boolean sameBeam(ParticleStack a, ParticleStack b) {
        return a.particleId().equals(b.particleId())
                && a.meanEnergyKeV() == b.meanEnergyKeV()
                && a.focus() == b.focus();
    }

    private static ParticleStack add(ParticleStack base, long amount) {
        long sum = base.amount() + amount;
        if (sum < 0) {
            sum = Long.MAX_VALUE;
        }
        return new ParticleStack(base.particleId(), sum, base.meanEnergyKeV(), base.focus());
    }

    private void roll(long now) {
        boolean consecutive = tick == now - 1;
        last = consecutive ? current : ParticleStack.EMPTY;
        tick = now;
        current = ParticleStack.EMPTY;
    }

    public ParticleStack beam() {
        if (level == null) {
            return ParticleStack.EMPTY;
        }
        long now = level.getGameTime();
        return tick == now ? last : tick == now - 1 ? current : ParticleStack.EMPTY;
    }

    @Override
    public HatchType getHatchType() {
        return ParticleHatches.TYPE;
    }

    @Override
    public boolean upgradesToSteel() {
        return false;
    }

    @Override
    public MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public void addJadeData(CompoundTag tag) {
        ParticleStack beam = beam();
        if (!beam.isEmpty()) {
            tag.putString(HatchKeys.PARTICLE_ID, beam.particleId().toString());
            tag.putLong(HatchKeys.PARTICLE_AMOUNT, beam.amount());
            tag.putLong(HatchKeys.PARTICLE_ENERGY, beam.meanEnergyKeV());
            tag.putDouble(HatchKeys.PARTICLE_FOCUS, beam.focus());
        } else {
            tag.putString(HatchKeys.PARTICLE_ID, "");
        }
    }
}
