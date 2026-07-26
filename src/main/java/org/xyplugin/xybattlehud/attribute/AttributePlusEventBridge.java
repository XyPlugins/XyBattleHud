package org.xyplugin.xybattlehud.attribute;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AttributePlusEventBridge implements Listener {
    private static final long MAX_AGE_MS = 1500L;
    private final XyBattleHudPlugin plugin;
    private final Map<UUID, TimedMessage> messages = new ConcurrentHashMap<>();
    private final Map<UUID, PendingHit> hits = new ConcurrentHashMap<>();
    private boolean available;

    public AttributePlusEventBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        HandlerList.unregisterAll(this);
        available = false;
        Plugin ap = plugin.getServer().getPluginManager().getPlugin("AttributePlus");
        if (ap == null || !ap.isEnabled()) return;
        ClassLoader loader = ap.getClass().getClassLoader();
        available |= register(loader, "org.serverct.ersha.api.event.AttrEntityDamageEvent", this::onDamage);
        register(loader, "org.serverct.ersha.api.event.attribute.AttrAttributeMessageEvent", this::onMessage);
    }

    @SuppressWarnings("unchecked")
    private boolean register(ClassLoader loader, String className, EventExecutor executor) {
        try {
            Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(className, true, loader).asSubclass(Event.class);
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR,
                    executor, plugin, true);
            return true;
        } catch (Throwable failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("未注册 AP 事件 " + className + ": " + failure.getMessage());
            return false;
        }
    }

    private void onMessage(Listener ignored, Event event) {
        Object entity = invoke(event, "getEntity");
        Object message = invoke(event, "getMessage");
        if (entity instanceof LivingEntity && message instanceof String) {
            messages.put(((LivingEntity) entity).getUniqueId(), new TimedMessage((String) message));
        }
    }

    private void onDamage(Listener ignored, Event event) {
        Object attacker = invoke(event, "getAttacker");
        Object target = invoke(event, "getTarget");
        Object damage = invoke(event, "getTargetDamage");
        if (!(attacker instanceof LivingEntity) || !(target instanceof LivingEntity)) return;
        UUID attackerId = ((LivingEntity) attacker).getUniqueId();
        TimedMessage message = messages.get(attackerId);
        String text = message != null && !message.expired() ? message.text : "";
        double amount = damage instanceof Number ? ((Number) damage).doubleValue() : 0.0;
        hits.put(((LivingEntity) target).getUniqueId(), new PendingHit(attackerId, amount, text));
    }

    public PendingHit consume(LivingEntity attacker, LivingEntity target) {
        PendingHit hit = hits.remove(target.getUniqueId());
        if (hit == null || hit.expired() || !hit.attacker.equals(attacker.getUniqueId())) return null;
        return hit;
    }

    private Object invoke(Object target, String name) {
        try {
            Method method = target.getClass().getMethod(name);
            return method.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean isAvailable() { return available; }

    public static final class PendingHit {
        private final UUID attacker;
        private final double damage;
        private final String message;
        private final long created = System.currentTimeMillis();

        private PendingHit(UUID attacker, double damage, String message) {
            this.attacker = attacker;
            this.damage = damage;
            this.message = message;
        }

        private boolean expired() { return System.currentTimeMillis() - created > MAX_AGE_MS; }
        public double getDamage() { return damage; }
        public String getMessage() { return message; }
    }

    private static final class TimedMessage {
        private final String text;
        private final long created = System.currentTimeMillis();
        private TimedMessage(String text) { this.text = text; }
        private boolean expired() { return System.currentTimeMillis() - created > MAX_AGE_MS; }
    }
}

