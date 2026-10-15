package com.breakinblocks.miei;

import java.util.function.BooleanSupplier;

public final class RunningCheck {
    private static boolean active;

    private RunningCheck() {}

    public static boolean active() {
        return active;
    }

    public static boolean run(BooleanSupplier check) {
        boolean previous = active;
        active = true;
        try {
            return check.getAsBoolean();
        } finally {
            active = previous;
        }
    }
}
