package org.xyplugin.xybattlehud.display;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.PluginSettings;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public final class HologramManager {
    private final XyBattleHudPlugin plugin;
    private final LinkedList<ActiveHologram> active = new LinkedList<>();
    private BukkitTask animationTask;

    public HologramManager(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        animationTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    public void show(LivingEntity target, String text) {
        PluginSettings settings = plugin.getSettings();
        while (active.size() >= settings.getMaxHolograms()) remove(active.removeFirst());
        double spread = settings.getRandomOffset();
        Location location = target.getLocation().add(0.0, target.getEyeHeight() + settings.getHeightOffset(), 0.0);
        if (spread > 0.0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            location.add(random.nextDouble(-spread, spread), random.nextDouble(0.0, spread),
                    random.nextDouble(-spread, spread));
        }
        ArmorStand stand = target.getWorld().spawn(location, ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setMarker(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setInvulnerable(true);
        if (settings.isDragonCoreHeadtagDamageRenderer()) {
            stand.setCustomName(settings.getHeadtagMarker() + text);
            stand.setCustomNameVisible(false);
        } else {
            stand.setCustomName(text);
            stand.setCustomNameVisible(true);
        }
        active.add(new ActiveHologram(stand, settings.getDurationTicks(), settings.getFloatSpeed()));
    }

    private void tick() {
        Iterator<ActiveHologram> iterator = active.iterator();
        while (iterator.hasNext()) {
            ActiveHologram hologram = iterator.next();
            if (!hologram.stand.isValid() || --hologram.remaining <= 0) {
                remove(hologram);
                iterator.remove();
                continue;
            }
            Location next = hologram.stand.getLocation().add(0.0, hologram.speed, 0.0);
            hologram.stand.teleport(next);
        }
    }

    public void clear() {
        for (ActiveHologram hologram : active) remove(hologram);
        active.clear();
    }

    public void shutdown() {
        clear();
        if (animationTask != null) animationTask.cancel();
    }

    public int size() { return active.size(); }

    private void remove(ActiveHologram hologram) {
        if (hologram.stand.isValid()) hologram.stand.remove();
    }

    private static final class ActiveHologram {
        private final ArmorStand stand;
        private int remaining;
        private final double speed;
        private ActiveHologram(ArmorStand stand, int remaining, double speed) {
            this.stand = stand;
            this.remaining = remaining;
            this.speed = speed;
        }
    }
}
