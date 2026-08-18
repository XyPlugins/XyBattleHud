package org.xyplugin.xybattlehud.pickup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.ExperienceSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AkariLevelExpBridge implements Listener {
    private final XyBattleHudPlugin plugin;
    private final Map<String, Long> recentExperiences = new ConcurrentHashMap<>();
    private boolean available;
    private int cleanupCounter;

    public AkariLevelExpBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        HandlerList.unregisterAll(this);
        recentExperiences.clear();
        cleanupCounter = 0;
        available = false;
        ExperienceSettings settings = plugin.getSettings().getPickup().getExperience();
        if (!plugin.getSettings().getPickup().isEnabled()
                || !settings.isEnabled() || !settings.isAkariLevelEnabled()) return;

        Plugin akariLevel = plugin.getServer().getPluginManager().getPlugin(settings.getProviderPlugin());
        if (akariLevel == null || !akariLevel.isEnabled()) return;
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(
                    settings.getEventClassName(), true,
                    akariLevel.getClass().getClassLoader()).asSubclass(Event.class);
            EventExecutor executor = this::onExperienceChange;
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR,
                    executor, plugin, true);
            available = true;
        } catch (Throwable failure) {
            if (plugin.isDebugEnabled()) {
                plugin.getLogger().warning("未接入 AkariLevel 经验事件: " + failure.getMessage());
            }
        }
    }

    private void onExperienceChange(Listener ignored, Event event) {
        ExperienceSettings settings = plugin.getSettings().getPickup().getExperience();
        long amount = number(read(event, settings.getAmountVariable()));
        if (!settings.isEnabled() || amount <= 0) return;

        String levelGroup = text(read(event, settings.getLevelGroupVariable()));
        if (!settings.acceptsLevelGroup(levelGroup)) return;
        String source = text(read(event, settings.getSourceVariable()));
        if (!settings.acceptsSource(source)) return;

        Player player = findPlayer(text(read(event, settings.getMemberVariable())));
        if (player == null) return;
        if (isDuplicateExperience(settings, player, amount, levelGroup)) {
            if (plugin.isDebugEnabled()) {
                plugin.getLogger().info("拾取视图(experience): 已合并重复经验提示 "
                        + player.getName() + " +" + amount);
            }
            return;
        }
        boolean sent = plugin.getPickupDisplays().showExperience(player, amount,
                settings.getDisplayName(), settings.getIconPath());
        if (plugin.isDebugEnabled() && sent) {
            plugin.getLogger().info("拾取视图(experience): " + player.getName()
                    + " +" + amount + " (" + source + ")");
        }
    }

    private boolean isDuplicateExperience(ExperienceSettings settings, Player player, long amount, String levelGroup) {
        int window = settings.getDedupeMillis();
        if (window <= 0) return false;
        long now = System.currentTimeMillis();
        String key = player.getUniqueId() + "|" + amount + "|" + levelGroup + "|"
                + settings.getDisplayName() + "|" + settings.getIconPath();
        Long previous = recentExperiences.get(key);
        if (previous != null && now - previous <= window) return true;
        recentExperiences.put(key, now);
        cleanupRecentExperiences(now, window);
        return false;
    }

    private void cleanupRecentExperiences(long now, int window) {
        cleanupCounter++;
        if (cleanupCounter < 64) return;
        cleanupCounter = 0;
        long expireBefore = now - Math.max(5000L, window * 4L);
        recentExperiences.entrySet().removeIf(entry -> entry.getValue() < expireBefore);
    }

    private Player findPlayer(String member) {
        if (member.isEmpty()) return null;
        try {
            Player player = Bukkit.getPlayer(UUID.fromString(member));
            if (player != null) return player;
        } catch (IllegalArgumentException ignored) {
            // AkariLevel 默认使用玩家名称作为 member，这里继续按名称查找。
        }
        Player exact = Bukkit.getPlayerExact(member);
        if (exact != null) return exact;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(member)) return player;
        }
        return null;
    }

    private Object read(Object target, String variable) {
        if (target == null || variable == null || variable.trim().isEmpty()) return null;
        String name = variable.trim();
        Object direct = invoke(target, name);
        if (direct != null) return direct;
        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        Object getter = invoke(target, "get" + capitalized);
        if (getter != null) return getter;
        Object booleanGetter = invoke(target, "is" + capitalized);
        if (booleanGetter != null) return booleanGetter;
        try {
            Field field = target.getClass().getDeclaredField(name);
            if (!field.isAccessible()) field.setAccessible(true);
            return field.get(target);
        } catch (Exception ignored) {
            return null;
        }
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

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private long number(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return value == null ? 0L : Long.parseLong(String.valueOf(value).trim());
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    public boolean isAvailable() {
        return available;
    }
}
