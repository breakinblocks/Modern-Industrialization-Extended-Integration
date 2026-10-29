package com.breakinblocks.miei.matter.replication;

import aztech.modern_industrialization.machines.MachineComponent;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterHandler;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class MatterTank implements IMatterHandler, MachineComponent.ServerOnly {
    private static final String CONTENTS = "matter_contents";

    private final boolean input;
    private final double capacity;
    private final Runnable onChange;
    private final Map<IMatterType, Double> stored = new LinkedHashMap<>();
    private List<IMatterType> slots;

    public MatterTank(boolean input, double capacity, Runnable onChange) {
        this.input = input;
        this.capacity = capacity;
        this.onChange = onChange;
    }

    public boolean isInput() {
        return input;
    }

    public List<MatterStack> contents() {
        List<MatterStack> out = new ArrayList<>();
        stored.forEach((type, amount) -> out.add(new MatterStack(type, amount)));
        return out;
    }

    public double capacity() {
        return capacity;
    }

    public double amountOf(IMatterType type) {
        return stored.getOrDefault(type, 0.0);
    }

    public double spaceFor(IMatterType type) {
        if (type == null || type == MatterType.EMPTY) {
            return 0;
        }
        return capacity - amountOf(type);
    }

    public double take(IMatterType type, double amount, boolean simulate) {
        double taken = Math.min(amount, amountOf(type));
        if (taken > 0 && !simulate) {
            set(type, amountOf(type) - taken);
        }
        return taken;
    }

    public double add(IMatterType type, double amount, boolean simulate) {
        double accepted = Math.min(amount, spaceFor(type));
        if (accepted > 0 && !simulate) {
            set(type, amountOf(type) + accepted);
        }
        return accepted;
    }

    private void set(IMatterType type, double amount) {
        if (amount <= 0) {
            stored.remove(type);
        } else {
            stored.put(type, amount);
        }
        onChange.run();
    }

    private List<IMatterType> slots() {
        if (slots != null) {
            return slots;
        }
        List<IMatterType> out = new ArrayList<>();
        Registry<IMatterType> registry = ReplicationRegistry.MATTER_TYPES_REGISTRY;
        if (registry == null) {
            for (MatterType type : MatterType.values()) {
                if (type != MatterType.EMPTY) {
                    out.add(type);
                }
            }
            return out;
        }
        for (IMatterType type : registry) {
            if (type != MatterType.EMPTY) {
                out.add(type);
            }
        }
        slots = out;
        return slots;
    }

    @Override
    public int getTanks() {
        return slots().size();
    }

    @Override
    public MatterStack getMatterInTank(int tank) {
        List<IMatterType> types = slots();
        if (tank < 0 || tank >= types.size()) {
            return MatterStack.EMPTY;
        }
        IMatterType type = types.get(tank);
        double amount = amountOf(type);
        return amount <= 0 ? MatterStack.EMPTY : new MatterStack(type, amount);
    }

    @Override
    public double getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isMatterValid(int tank, MatterStack stack) {
        List<IMatterType> types = slots();
        return stack != null
                && !stack.isEmpty()
                && tank >= 0
                && tank < types.size()
                && types.get(tank) == stack.getMatterType();
    }

    @Override
    public double fill(MatterStack resource, IFluidHandler.FluidAction action) {
        if (!input || resource == null || resource.isEmpty()) {
            return 0;
        }
        return add(resource.getMatterType(), resource.getAmount(), action.simulate());
    }

    @Override
    public MatterStack drain(MatterStack resource, IFluidHandler.FluidAction action) {
        if (input || resource == null || resource.isEmpty()) {
            return MatterStack.EMPTY;
        }
        IMatterType type = resource.getMatterType();
        double taken = take(type, resource.getAmount(), action.simulate());
        return taken <= 0 ? MatterStack.EMPTY : new MatterStack(type, taken);
    }

    @Override
    public MatterStack drain(double amount, IFluidHandler.FluidAction action) {
        if (input || stored.isEmpty()) {
            return MatterStack.EMPTY;
        }
        IMatterType type = stored.keySet().iterator().next();
        double taken = take(type, amount, action.simulate());
        return taken <= 0 ? MatterStack.EMPTY : new MatterStack(type, taken);
    }

    @Override
    public void writeNbt(CompoundTag tag, HolderLookup.Provider registries) {
        if (stored.isEmpty()) {
            return;
        }
        ListTag list = new ListTag();
        for (MatterStack stack : contents()) {
            list.add(stack.writeToNBT(new CompoundTag()));
        }
        tag.put(CONTENTS, list);
    }

    @Override
    public void readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean upgrading) {
        stored.clear();
        for (Tag entry : tag.getList(CONTENTS, Tag.TAG_COMPOUND)) {
            MatterStack stack = MatterStack.loadMatterStackFromNBT((CompoundTag) entry);
            if (!stack.isEmpty() && stack.getAmount() > 0) {
                stored.merge(stack.getMatterType(), stack.getAmount(), Double::sum);
            }
        }
    }
}
