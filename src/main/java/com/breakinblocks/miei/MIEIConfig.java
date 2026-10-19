package com.breakinblocks.miei;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MIEIConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue CHRONON_ITEM_SLOT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CHRONON_ITEM_SLOT = builder.comment(
                        "Give the Chronon Input Hatch an item slot that drains a charged chronon item such as a Tempad Chronon Cell or Battery.",
                        "When false the hatch only draws from a Tempad Metronome within range.")
                .define("chronon_item_slot", true);
        SPEC = builder.build();
    }

    private MIEIConfig() {}

    public static boolean chrononItemSlot() {
        return !SPEC.isLoaded() || CHRONON_ITEM_SLOT.get();
    }
}
