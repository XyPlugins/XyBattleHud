package org.xyplugin.xybattlehud.combo;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.ComboSettings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class ComboDisplayManager {
    private final XyBattleHudPlugin plugin;
    private final Map<UUID, ActiveDisplay> active = new HashMap<>();
    private DragonCoreComboBridge bridge;
    private BukkitTask cleanupTask;

    public ComboDisplayManager(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        bridge = new DragonCoreComboBridge(plugin);
        cleanupTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    public void show(Player attacker, int count, boolean critical) {
        ComboSettings settings = plugin.getSettings().getCombo();
        if (!settings.isEnabled() || !bridge.isAvailable() || count < settings.getDisplayFrom()) return;
        if (bridge.show(attacker, settings, count, critical)) {
            active.put(attacker.getUniqueId(), new ActiveDisplay(settings.getHudName(),
                    settings.getClearFunctionName(), settings.getTimeoutTicks()));
        }
    }

    private void tick() {
        Iterator<Map.Entry<UUID, ActiveDisplay>> iterator = active.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ActiveDisplay> entry = iterator.next();
            if (--entry.getValue().remaining <= 0) {
                clearDisplay(entry.getKey(), entry.getValue());
                iterator.remove();
            }
        }
    }

    public void reload() {
        clear();
        bridge.discover();
    }

    public void clear() {
        for (Map.Entry<UUID, ActiveDisplay> entry : active.entrySet()) {
            clearDisplay(entry.getKey(), entry.getValue());
        }
        active.clear();
    }

    public void remove(UUID attacker) {
        ActiveDisplay display = active.remove(attacker);
        if (display != null) clearDisplay(attacker, display);
    }

    public void shutdown() {
        clear();
        if (cleanupTask != null) cleanupTask.cancel();
    }

    public boolean isAvailable() { return bridge.isAvailable(); }
    public int size() { return active.size(); }

    private void clearDisplay(UUID playerId, ActiveDisplay display) {
        bridge.clear(plugin.getServer().getPlayer(playerId), display.hudName, display.clearFunctionName);
    }

    private static final class ActiveDisplay {
        private final String hudName;
        private final String clearFunctionName;
        private int remaining;

        private ActiveDisplay(String hudName, String clearFunctionName, int remaining) {
            this.hudName = hudName;
            this.clearFunctionName = clearFunctionName;
            this.remaining = remaining;
        }
    }
}
