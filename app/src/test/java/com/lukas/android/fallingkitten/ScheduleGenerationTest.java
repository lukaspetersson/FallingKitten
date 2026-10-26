package com.lukas.android.fallingkitten;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ScheduleGenerationTest {
    @Test public void inactiveGuardRejectsDefaultAndUnknownTokens() {
        ScheduleGeneration guard = new ScheduleGeneration();
        assertFalse(guard.accepts(0));
        assertFalse(guard.accepts(1));
    }

    @Test public void acceleratorRestartInvalidatesPriorActiveGeneration() {
        ScheduleGeneration guard = new ScheduleGeneration();
        long priorToken = guard.activate();
        long restartedToken = guard.activate(); // canonical restart has no pause invalidation
        assertNotEquals(priorToken, restartedToken);
        assertFalse(guard.accepts(priorToken));
        assertTrue(guard.accepts(restartedToken));
    }

    @Test public void initialTokenIsAccepted() {
        ScheduleGeneration guard = new ScheduleGeneration();
        long token = guard.activate();
        assertTrue(guard.accepts(token));
    }

    @Test public void invalidationRejectsSpawnAndAcceleratorUseOfToken() {
        ScheduleGeneration guard = new ScheduleGeneration();
        long token = guard.activate();
        guard.invalidate();
        assertFalse(guard.accepts(token)); // spawn
        assertFalse(guard.accepts(token)); // accelerator
    }

    @Test public void resumeAcceptsNewTokenButNeverOldToken() {
        ScheduleGeneration guard = new ScheduleGeneration();
        long oldToken = guard.activate();
        guard.invalidate();
        long resumedToken = guard.activate();
        assertNotEquals(oldToken, resumedToken);
        assertFalse(guard.accepts(oldToken));
        assertTrue(guard.accepts(resumedToken));
    }

    @Test public void duplicateInvalidationIsIdempotent() {
        ScheduleGeneration guard = new ScheduleGeneration();
        long token = guard.activate();
        guard.invalidate();
        guard.invalidate();
        assertFalse(guard.accepts(token));
    }

    @Test public void obsoleteAcceleratorCannotRequestRestart() {
        ScheduleGeneration guard = new ScheduleGeneration();
        long acceleratorToken = guard.activate();
        guard.invalidate();
        guard.activate();
        assertFalse(guard.accepts(acceleratorToken));
    }
}
