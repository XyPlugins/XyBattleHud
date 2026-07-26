package org.xyplugin.xybattlehud.combo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ComboTracker {
    private static final int ABSOLUTE_MAX_COUNT = 999;
    private static final long DUPLICATE_WINDOW_MS = 50L;
    private final long timeoutMillis;
    private final int maxCount;
    private final Map<UUID, State> states = new HashMap<>();

    public ComboTracker(int timeoutTicks, int maxCount) {
        timeoutMillis = Math.max(1, timeoutTicks) * 50L;
        this.maxCount = Math.min(ABSOLUTE_MAX_COUNT, Math.max(1, maxCount));
    }

    public int record(UUID attacker, UUID target, long now) {
        State state = states.get(attacker);
        if (state != null && state.target.equals(target) && now - state.lastHit < DUPLICATE_WINDOW_MS) {
            return 0;
        }
        if (state == null || !state.target.equals(target) || now - state.lastHit > timeoutMillis) {
            states.put(attacker, new State(target, 1, now));
            return 1;
        }
        state.count = Math.min(maxCount, state.count + 1);
        state.lastHit = now;
        return state.count;
    }

    public void remove(UUID attacker) {
        states.remove(attacker);
    }

    public void clear() {
        states.clear();
    }

    private static final class State {
        private final UUID target;
        private int count;
        private long lastHit;

        private State(UUID target, int count, long lastHit) {
            this.target = target;
            this.count = count;
            this.lastHit = lastHit;
        }
    }
}
