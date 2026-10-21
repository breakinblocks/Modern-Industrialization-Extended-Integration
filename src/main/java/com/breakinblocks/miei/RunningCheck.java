package com.breakinblocks.miei;

import java.util.function.BooleanSupplier;

public final class RunningCheck {
    private static boolean active;
    private static int multiplier = 1;

    private RunningCheck() {}

    public static boolean active() {
        return active;
    }

    public static int multiplier() {
        return active ? multiplier : 1;
    }

    public static boolean run(BooleanSupplier check) {
        return run(1, check);
    }

    public static boolean run(int crafts, BooleanSupplier check) {
        boolean previousActive = active;
        int previousMultiplier = multiplier;
        active = true;
        multiplier = Math.max(1, crafts);
        try {
            return check.getAsBoolean();
        } finally {
            active = previousActive;
            multiplier = previousMultiplier;
        }
    }
}
