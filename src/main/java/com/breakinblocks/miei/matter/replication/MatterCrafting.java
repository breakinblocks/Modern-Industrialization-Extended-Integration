package com.breakinblocks.miei.matter.replication;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import com.breakinblocks.miei.matter.MatterCondition;
import com.breakinblocks.miei.mixin.MultiblockMachineBlockEntityAccessor;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.block.tile.NetworkBlockEntity;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class MatterCrafting {
    private MatterCrafting() {}

    public static IMatterType type(MatterCondition condition) {
        String name = condition.matter();
        Registry<IMatterType> registry = ReplicationRegistry.MATTER_TYPES_REGISTRY;
        if (registry != null) {
            ResourceLocation id = ResourceLocation.tryParse(name.toLowerCase(Locale.ROOT));
            if (name.indexOf(':') >= 0 && id != null && registry.containsKey(id)) {
                return registry.get(id);
            }
            for (IMatterType type : registry) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        for (MatterType type : MatterType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return MatterType.EMPTY;
    }

    public static List<MatterHatchBlockEntity> hatches(MachineBlockEntity machine, boolean input) {
        if (!(machine instanceof MultiblockMachineBlockEntity multiblock)) {
            return List.of();
        }
        ShapeMatcher matcher = ((MultiblockMachineBlockEntityAccessor) multiblock).miei$getShapeMatcher();
        if (matcher == null) {
            return List.of();
        }
        List<MatterHatchBlockEntity> out = new ArrayList<>();
        for (HatchBlockEntity hatch : matcher.getMatchedHatches()) {
            if (hatch instanceof MatterHatchBlockEntity matterHatch && matterHatch.isInput() == input) {
                out.add(matterHatch);
            }
        }
        return out;
    }

    public static List<MatterNetwork> networks(List<MatterHatchBlockEntity> hatches) {
        List<MatterNetwork> out = new ArrayList<>();
        for (MatterHatchBlockEntity hatch : hatches) {
            Level level = hatch.getLevel();
            if (level == null) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                BlockPos neighbour = hatch.getBlockPos().relative(direction);
                if (!level.isLoaded(neighbour)) {
                    continue;
                }
                if (level.getBlockEntity(neighbour) instanceof NetworkBlockEntity<?> node) {
                    MatterNetwork network = node.getNetwork();
                    if (network != null && !out.contains(network)) {
                        out.add(network);
                    }
                }
            }
        }
        return out;
    }

    public static List<IMatterTank> networkTanks(List<MatterNetwork> networks, boolean input) {
        List<IMatterTank> out = new ArrayList<>();
        for (MatterNetwork network : networks) {
            List<NetworkElement> elements = new ArrayList<>(network.getMatterStacksHolders());
            elements.addAll(input ? network.getMatterStacksSuppliers() : network.getMatterStacksConsumers());
            for (NetworkElement element : elements) {
                Level level = element.getLevel();
                if (level == null || !level.isLoaded(element.getPos())) {
                    continue;
                }
                BlockEntity blockEntity = level.getBlockEntity(element.getPos());
                List<? extends IMatterTank> tanks = null;
                if (input && blockEntity instanceof IMatterTanksSupplier supplier) {
                    tanks = supplier.getTanks();
                } else if (!input && blockEntity instanceof IMatterTanksConsumer consumer) {
                    tanks = consumer.getTanks();
                }
                if (tanks == null) {
                    continue;
                }
                for (IMatterTank tank : tanks) {
                    if (!out.contains(tank)) {
                        out.add(tank);
                    }
                }
            }
        }
        return out;
    }

    private static double held(IMatterTank tank, IMatterType type) {
        MatterStack stored = tank.getMatter();
        return stored == null || stored.isEmpty() || stored.getMatterType() != type ? 0 : stored.getAmount();
    }

    private static double room(IMatterTank tank, IMatterType type) {
        return tank.fill(new MatterStack(type, tank.getCapacity()), IFluidHandler.FluidAction.SIMULATE);
    }

    public static boolean canProcess(MachineBlockEntity machine, MatterCondition condition) {
        return condition.output()
                ? space(machine, condition) >= condition.amount()
                : available(machine, condition) >= condition.amount();
    }

    public static double available(MachineBlockEntity machine, MatterCondition condition) {
        IMatterType type = type(condition);
        List<MatterHatchBlockEntity> hatches = hatches(machine, true);
        double total = 0;
        for (MatterHatchBlockEntity hatch : hatches) {
            total += hatch.tank().amountOf(type);
        }
        for (IMatterTank tank : networkTanks(networks(hatches), true)) {
            total += held(tank, type);
        }
        return total;
    }

    public static double space(MachineBlockEntity machine, MatterCondition condition) {
        IMatterType type = type(condition);
        List<MatterHatchBlockEntity> hatches = hatches(machine, false);
        double total = 0;
        for (MatterHatchBlockEntity hatch : hatches) {
            total += hatch.tank().spaceFor(type);
        }
        for (IMatterTank tank : networkTanks(networks(hatches), false)) {
            total += room(tank, type);
        }
        return total;
    }

    public static int maxCrafts(MachineBlockEntity machine, MachineRecipe recipe) {
        double max = Integer.MAX_VALUE;
        for (MatterCondition condition : conditions(recipe, false)) {
            if (condition.amount() > 0) {
                max = Math.min(max, Math.floor(available(machine, condition) / condition.amount()));
            }
        }
        for (MatterCondition condition : conditions(recipe, true)) {
            if (condition.amount() > 0) {
                max = Math.min(max, Math.floor(space(machine, condition) / condition.amount()));
            }
        }
        return (int) max;
    }

    public static boolean take(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate, int multiplier) {
        for (MatterCondition condition : conditions(recipe, false)) {
            double amount = condition.amount() * multiplier;
            if (available(machine, condition) < amount) {
                return false;
            }
            if (!simulate) {
                IMatterType type = type(condition);
                List<MatterHatchBlockEntity> hatches = hatches(machine, true);
                double remaining = amount;
                for (MatterHatchBlockEntity hatch : hatches) {
                    remaining -= hatch.tank().take(type, remaining, false);
                    if (remaining <= 0) {
                        break;
                    }
                }
                if (remaining > 0) {
                    List<MatterNetwork> networks = networks(hatches);
                    for (IMatterTank tank : networkTanks(networks, true)) {
                        double share = Math.min(remaining, held(tank, type));
                        if (share > 0) {
                            remaining -= tank.drain(new MatterStack(type, share), IFluidHandler.FluidAction.EXECUTE)
                                    .getAmount();
                        }
                        if (remaining <= 0) {
                            break;
                        }
                    }
                    networks.forEach(network -> network.onTankValueChanged(type));
                }
            }
        }
        return true;
    }

    public static boolean put(MachineBlockEntity machine, MachineRecipe recipe, boolean simulate, int multiplier) {
        for (MatterCondition condition : conditions(recipe, true)) {
            double amount = condition.amount() * multiplier;
            if (space(machine, condition) < amount) {
                return false;
            }
            if (!simulate) {
                IMatterType type = type(condition);
                List<MatterHatchBlockEntity> hatches = hatches(machine, false);
                double remaining = amount;
                for (MatterHatchBlockEntity hatch : hatches) {
                    remaining -= hatch.tank().add(type, remaining, false);
                    if (remaining <= 0) {
                        break;
                    }
                }
                if (remaining > 0) {
                    List<MatterNetwork> networks = networks(hatches);
                    for (IMatterTank tank : networkTanks(networks, false)) {
                        remaining -= tank.fill(new MatterStack(type, remaining), IFluidHandler.FluidAction.EXECUTE);
                        if (remaining <= 0) {
                            break;
                        }
                    }
                    networks.forEach(network -> network.onTankValueChanged(type));
                }
            }
        }
        return true;
    }

    private static List<MatterCondition> conditions(MachineRecipe recipe, boolean output) {
        List<MatterCondition> out = new ArrayList<>();
        for (MachineProcessCondition condition : recipe.conditions) {
            if (condition instanceof MatterCondition matter && matter.output() == output) {
                out.add(matter);
            }
        }
        return out;
    }
}
