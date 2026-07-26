package org.xyplugin.xybattlehud.attribute;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AttributePlusEventBridge implements Listener {
    private static final long MAX_AGE_MS = 1500L;
    private final XyBattleHudPlugin plugin;
    private final Map<UUID, TimedMessage> messages = new ConcurrentHashMap<>();
    private final Map<UUID, TriggerContext> triggers = new ConcurrentHashMap<>();
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
        register(loader, "org.serverct.ersha.api.event.attribute.AttrAttributeTriggerEvent$After", this::onTrigger);
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
        UUID targetId = ((LivingEntity) target).getUniqueId();
        TimedMessage message = messages.get(attackerId);
        String text = message != null && !message.expired() ? message.text : "";
        TriggerContext triggerContext = triggers.remove(targetId);
        Set<String> triggered = triggerContext != null && triggerContext.matches(attackerId)
                ? triggerContext.values : Collections.<String>emptySet();
        double amount = damage instanceof Number ? ((Number) damage).doubleValue() : 0.0;
        hits.put(targetId, new PendingHit(attackerId, amount, text, triggered));
    }

    private void onTrigger(Listener ignored, Event event) {
        Object subAttribute = invoke(event, "getSubAttribute");
        Object handle = invoke(event, "getAttributeHandle");
        if (subAttribute == null || handle == null || !wasTriggered(subAttribute)) return;
        Object attacker = invoke(handle, "getAttackerOrKiller");
        Object target = invoke(handle, "getEntity");
        if (!(attacker instanceof LivingEntity) || !(target instanceof LivingEntity)) return;
        UUID attackerId = ((LivingEntity) attacker).getUniqueId();
        UUID targetId = ((LivingEntity) target).getUniqueId();
        TriggerContext context = triggers.compute(targetId, (id, current) ->
                current != null && current.matches(attackerId) ? current : new TriggerContext(attackerId));
        addTrigger(context.values, invoke(subAttribute, "getPlaceholder"));
        addTrigger(context.values, invoke(subAttribute, "getAttributeName"));
    }

    private boolean wasTriggered(Object subAttribute) {
        try {
            Field field = subAttribute.getClass().getField("trigger");
            return field.getBoolean(subAttribute);
        } catch (Exception ignored) {
            return false;
        }
    }

    private void addTrigger(Set<String> values, Object value) {
        if (value instanceof String && !((String) value).trim().isEmpty()) {
            values.add(((String) value).trim().toLowerCase(java.util.Locale.ROOT));
        }
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
        private final Set<String> triggers;
        private final long created = System.currentTimeMillis();

        private PendingHit(UUID attacker, double damage, String message, Set<String> triggers) {
            this.attacker = attacker;
            this.damage = damage;
            this.message = message;
            this.triggers = Collections.unmodifiableSet(new HashSet<>(triggers));
        }

        private boolean expired() { return System.currentTimeMillis() - created > MAX_AGE_MS; }
        public double getDamage() { return damage; }
        public String getMessage() { return message; }
        public Set<String> getTriggers() { return triggers; }
    }

    private static final class TimedMessage {
        private final String text;
        private final long created = System.currentTimeMillis();
        private TimedMessage(String text) { this.text = text; }
        private boolean expired() { return System.currentTimeMillis() - created > MAX_AGE_MS; }
    }

    private static final class TriggerContext {
        private final UUID attacker;
        private final Set<String> values = ConcurrentHashMap.newKeySet();
        private final long created = System.currentTimeMillis();
        private TriggerContext(UUID attacker) { this.attacker = attacker; }
        private boolean matches(UUID attacker) {
            return this.attacker.equals(attacker) && System.currentTimeMillis() - created <= MAX_AGE_MS;
        }
    }
}
