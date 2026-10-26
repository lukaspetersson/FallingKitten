package com.lukas.android.fallingkitten;

/** Main-thread-owned generation guard for Timer work posted to the activity Handler. */
final class ScheduleGeneration {
    private long generation;
    private boolean active;

    long activate() {
        generation++;
        active = true;
        return generation;
    }

    void invalidate() {
        active = false;
    }

    boolean accepts(long token) {
        return active && token == generation;
    }
}
