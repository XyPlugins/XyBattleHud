package org.xyplugin.xybattlehud.pickup;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.MoneySettings;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MythicMobsMoneyBridge implements Listener {
    private static final String[] EVENT_CLASSES = {
            "io.lumine.mythic.bukkit.events.MythicMobLootDropEvent",
            "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobLootDropEvent"
    };

    private final XyBattleHudPlugin plugin;
    private final Map<String, Long> recentMoney = new ConcurrentHashMap<>();
    private boolean available;
    private String eventClassName = "";
    private int cleanupCounter;

    public MythicMobsMoneyBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        HandlerList.unregisterAll(this);
        recentMoney.clear();
        cleanupCounter = 0;
        available = false;
        eventClassName = "";

        MoneySettings settings = plugin.getSettings().getPickup().getMoney();
        if (!plugin.getSettings().getPickup().isEnabled()
                || !settings.isEnabled() || !settings.isMythicMobsEnabled()) return;

        Plugin mythicMobs = plugin.getServer().getPluginManager().getPlugin(settings.getProviderPlugin());
        if (mythicMobs == null || !mythicMobs.isEnabled()) return;

        for (String candidate : EVENT_CLASSES) {
            if (tryRegister(mythicMobs, candidate)) return;
        }
        if (plugin.isDebugEnabled()) {
            plugin.getLogger().warning("未接入 MythicMobs 金币事件: 未找到 MythicMobLootDropEvent");
        }
    }

    private boolean tryRegister(Plugin mythicMobs, String candidate) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(candidate, true,
                    mythicMobs.getClass().getClassLoader()).asSubclass(Event.class);
            EventExecutor executor = this::onLootDrop;
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR,
                    executor, plugin, true);
            available = true;
            eventClassName = candidate;
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void onLootDrop(Listener ignored, Event event) {
        MoneySettings settings = plugin.getSettings().getPickup().getMoney();
        if (!settings.isEnabled()) return;

        long amount = number(invoke(event, "getMoney"));
        if (amount <= 0) return;

        Player player = findPlayer(invoke(event, "getKiller"));
        if (player == null) return;

        String mobId = entityId(invoke(event, "getEntity"));
        if (isDuplicateMoney(settings, player, mobId, amount)) {
            if (plugin.isDebugEnabled()) {
                plugin.getLogger().info("拾取视图(money): 已合并重复金币提示 "
                        + player.getName() + " +" + amount);
            }
            return;
        }

        boolean sent = plugin.getPickupDisplays().showMoney(player, amount,
                settings.getDisplayName(), settings.getIconPath());
        if (plugin.isDebugEnabled() && sent) {
            plugin.getLogger().info("拾取视图(money): " + player.getName() + " +" + amount);
        }
    }

    private Player findPlayer(Object killer) {
        if (killer instanceof Player) return (Player) killer;
        if (killer instanceof Tameable) {
            Object owner = ((Tameable) killer).getOwner();
            if (owner instanceof Player) return (Player) owner;
        }
        Object bukkitEntity = invoke(killer, "getBukkitEntity");
        if (bukkitEntity != null && bukkitEntity != killer) return findPlayer(bukkitEntity);
        return null;
    }

    private String entityId(Object entity) {
        if (entity instanceof Entity) return ((Entity) entity).getUniqueId().toString();
        Object uuid = invoke(entity, "getUniqueId");
        return uuid == null ? "" : String.valueOf(uuid);
    }

    private boolean isDuplicateMoney(MoneySettings settings, Player player, String mobId, long amount) {
        int window = settings.getDedupeMillis();
        if (window <= 0) return false;
        long now = System.currentTimeMillis();
        String key = player.getUniqueId() + "|" + mobId + "|" + amount + "|"
                + settings.getDisplayName() + "|" + settings.getIconPath();
        Long previous = recentMoney.get(key);
        if (previous != null && now - previous <= window) return true;
        recentMoney.put(key, now);
        cleanupRecentMoney(now, window);
        return false;
    }

    private void cleanupRecentMoney(long now, int window) {
        cleanupCounter++;
        if (cleanupCounter < 64) return;
        cleanupCounter = 0;
        long expireBefore = now - Math.max(5000L, window * 4L);
        recentMoney.entrySet().removeIf(entry -> entry.getValue() < expireBefore);
    }

    private Object invoke(Object target, String name) {
        if (target == null) return null;
        try {
            Method method = target.getClass().getMethod(name);
            if (!method.isAccessible()) method.setAccessible(true);
            return method.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private long number(Object value) {
        if (value instanceof Number) return Math.round(((Number) value).doubleValue());
        try {
            return value == null ? 0L : Math.round(Double.parseDouble(String.valueOf(value).trim()));
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public String getEventClassName() {
        return eventClassName;
    }
}
