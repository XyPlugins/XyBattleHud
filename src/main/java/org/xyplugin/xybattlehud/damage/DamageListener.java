package org.xyplugin.xybattlehud.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.attribute.AttributePlusEventBridge;
import org.xyplugin.xybattlehud.config.PluginSettings;

public final class DamageListener implements Listener {
    private final XyBattleHudPlugin plugin;

    public DamageListener(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        Player attacker = attacker(event.getDamager());
        if (attacker == null) return;
        LivingEntity target = (LivingEntity) event.getEntity();
        PluginSettings settings = plugin.getSettings();
        if (settings.getIgnoredWorlds().contains(target.getWorld().getName())) return;
        if (settings.getIgnoredEntityTypes().contains(target.getType())) return;
        if (settings.isIgnoreSelfDamage() && attacker.getUniqueId().equals(target.getUniqueId())) return;

        AttributePlusEventBridge.PendingHit pending = plugin.getAttributePlusBridge().consume(attacker, target);
        double damage = pending != null && pending.getDamage() > 0.0 ? pending.getDamage() : event.getFinalDamage();
        if (damage < settings.getMinDamage()) return;
        String message = pending == null ? "" : pending.getMessage();
        java.util.Set<String> apTriggers = pending == null
                ? java.util.Collections.<String>emptySet() : pending.getTriggers();
        boolean direct = event.getDamager() instanceof Player;
        DamageType type = plugin.getResolver().resolve(attacker, target, message, apTriggers, direct);
        int combo = 0;
        if (settings.getCombo().isEnabled()) {
            combo = plugin.getComboTracker().record(attacker.getUniqueId(), target.getUniqueId(),
                    System.currentTimeMillis());
            if (combo >= settings.getCombo().getDisplayFrom()) {
                plugin.getComboDisplays().show(attacker, target, combo, "critical".equals(type.getId()));
            }
        }
        String number = DamageNumberFormatter.format(damage, settings.getDecimalPlaces(), settings.getThousandsSeparator());
        String text = type.getColor() + type.getPrefix()
                + FontMapper.map(number, type.getDigits(), type.getSymbols()) + type.getSuffix();
        plugin.getHolograms().show(target, text);
        if (plugin.isDebugEnabled()) {
            plugin.getLogger().info("伤害: " + attacker.getName() + " -> " + target.getName()
                    + ", value=" + damage + ", type=" + type.getId()
                    + ", combo=" + combo + ", apTriggers=" + apTriggers + ", apMessage=" + message);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getComboTracker().remove(event.getPlayer().getUniqueId());
        plugin.getComboDisplays().remove(event.getPlayer().getUniqueId());
    }

    private Player attacker(Entity damager) {
        if (damager instanceof Player) return (Player) damager;
        if (damager instanceof Projectile) {
            ProjectileSource source = ((Projectile) damager).getShooter();
            if (source instanceof Player) return (Player) source;
        }
        return null;
    }
}
