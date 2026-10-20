package com.breakinblocks.miei.stage;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.breakinblocks.miei.compat.HatchKeys;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public final class WaitingStages {
    private static final long FRESH_TICKS = 120;
    private static final Map<MachineBlockEntity, Map<String, Long>> WAITING =
            Collections.synchronizedMap(new WeakHashMap<>());

    private WaitingStages() {}

    static void update(MachineBlockEntity machine, String stage, boolean met, long now) {
        if (met) {
            Map<String, Long> stages = WAITING.get(machine);
            if (stages != null) {
                stages.remove(stage);
            }
        } else {
            WAITING.computeIfAbsent(machine, key -> new ConcurrentHashMap<>()).put(stage, now);
        }
    }

    public static void addJadeData(MachineBlockEntity machine, CompoundTag tag) {
        Map<String, Long> stages = WAITING.get(machine);
        if (stages == null || stages.isEmpty() || machine.getLevel() == null) {
            return;
        }
        long now = machine.getLevel().getGameTime();
        stages.values().removeIf(tick -> now - tick > FRESH_TICKS);
        if (stages.isEmpty()) {
            return;
        }
        ListTag list = new ListTag();
        stages.keySet().stream().sorted().map(StringTag::valueOf).forEach(list::add);
        tag.put(HatchKeys.STAGE_WAITING, list);
        if (machine.placedBy.placerId == null) {
            tag.putBoolean(HatchKeys.STAGE_NO_OWNER, true);
        }
    }
}
