package org.xyplugin.xybattlehud.combo;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ComboTrackerTest {
    @Test
    public void countsHitsAndIgnoresDuplicateDamageEvents() {
        ComboTracker tracker = new ComboTracker(40, 99);
        UUID attacker = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        assertEquals(1, tracker.record(attacker, target, 1000L));
        assertEquals(0, tracker.record(attacker, target, 1020L));
        assertEquals(2, tracker.record(attacker, target, 1100L));
    }

    @Test
    public void resetsAfterTimeoutOrTargetChange() {
        ComboTracker tracker = new ComboTracker(40, 99);
        UUID attacker = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        assertEquals(1, tracker.record(attacker, first, 1000L));
        assertEquals(1, tracker.record(attacker, first, 3101L));
        assertEquals(1, tracker.record(attacker, second, 3200L));
    }

    @Test
    public void stopsAtConfiguredMaximum() {
        ComboTracker tracker = new ComboTracker(40, 2);
        UUID attacker = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        assertEquals(1, tracker.record(attacker, target, 1000L));
        assertEquals(2, tracker.record(attacker, target, 1100L));
        assertEquals(2, tracker.record(attacker, target, 1200L));
    }

    @Test
    public void neverCountsPast999() {
        ComboTracker tracker = new ComboTracker(40, 5000);
        UUID attacker = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        int count = 0;
        for (int i = 0; i < 1100; i++) {
            count = tracker.record(attacker, target, 1000L + i * 100L);
        }
        assertEquals(999, count);
    }
}
