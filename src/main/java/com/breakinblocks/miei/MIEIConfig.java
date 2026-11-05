package com.breakinblocks.miei;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MIEIConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue CHRONON_ITEM_SLOT;
    private static final ModConfigSpec.BooleanValue STACK_STRESS_HATCHES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CHRONON_ITEM_SLOT = builder.comment(
                        "Give the Chronon Input Hatch an item slot that drains a charged chronon item such as a Tempad Chronon Cell or Battery.",
                        "When false the hatch only draws from a Tempad Metronome within range.")
                .define("chronon_item_slot", true);
        STACK_STRESS_HATCHES = builder.comment(
                        "Add up the spare stress of every Stress Input Hatch on a multiblock, one share per kinetic network, and split the load between them.",
                        "When false only the hatch with the most spare stress counts and carries the whole load, so a machine cannot draw more than one network's capacity.")
                .define("stack_stress_hatches", true);
        SPEC = builder.build();
    }

    private MIEIConfig() {}

    public static boolean chrononItemSlot() {
        return !SPEC.isLoaded() || CHRONON_ITEM_SLOT.get();
    }

    public static boolean stackStressHatches() {
        return !SPEC.isLoaded() || STACK_STRESS_HATCHES.get();
    }
}
