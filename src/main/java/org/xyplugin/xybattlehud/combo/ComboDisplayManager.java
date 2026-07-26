package org.xyplugin.xybattlehud.combo;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.ComboSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class ComboDisplayManager {
    private static final double VIEW_DISTANCE_SQUARED = 32.0 * 32.0;
    private final XyBattleHudPlugin plugin;
    private final Map<UUID, ActiveDisplay> active = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private DragonCoreTextureBridge bridge;
    private BukkitTask cleanupTask;

    public ComboDisplayManager(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        bridge = new DragonCoreTextureBridge(plugin);
        cleanupTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    public void show(Player attacker, LivingEntity target, int count, boolean critical) {
        ComboSettings settings = plugin.getSettings().getCombo();
        if (!settings.isEnabled() || !bridge.isAvailable() || count < settings.getDisplayFrom()) return;
        remove(attacker.getUniqueId());

        String text = Integer.toString(count);
        String folder = critical ? settings.getCriticalDigitsFolder() : settings.getNormalDigitsFolder();
        String label = critical ? settings.getCriticalLabel() : settings.getNormalLabel();
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            pieces.add(new Piece(folder + "/" + text.charAt(i) + ".png",
                    settings.getNumberWidth(), settings.getNumberHeight()));
        }
        pieces.add(new Piece(label, settings.getLabelWidth(), settings.getLabelHeight()));

        ActiveDisplay display = new ActiveDisplay(plugin.getSettings().getDurationTicks());
        long id = sequence.incrementAndGet();
        Location anchor = target.getLocation().add(0.0, target.getEyeHeight(), 0.0);
        for (Player viewer : plugin.getServer().getOnlinePlayers()) {
            if (!viewer.getWorld().equals(target.getWorld())
                    || viewer.getLocation().distanceSquared(anchor) > VIEW_DISTANCE_SQUARED) continue;
            sendToViewer(display, viewer, attacker, anchor, pieces, settings, id);
        }
        if (!display.textures.isEmpty()) active.put(attacker.getUniqueId(), display);
    }

    private void sendToViewer(ActiveDisplay display, Player viewer, Player attacker, Location anchor,
                              List<Piece> pieces, ComboSettings settings, long id) {
        Vector forward = viewer.getEyeLocation().toVector().subtract(anchor.toVector()).setY(0.0);
        if (forward.lengthSquared() < 0.0001) forward = new Vector(0.0, 0.0, 1.0);
        else forward.normalize();
        Vector right = new Vector(forward.getZ(), 0.0, -forward.getX());
        Location center = anchor.clone()
                .add(right.clone().multiply(settings.getOffsetX()))
                .add(0.0, settings.getOffsetY(), 0.0)
                .add(forward.clone().multiply(settings.getOffsetZ()));

        double totalWidth = 0.0;
        for (Piece piece : pieces) totalWidth += piece.width;
        totalWidth += settings.getSpacing() * Math.max(0, pieces.size() - 1);
        double cursor = -totalWidth / 2.0;
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            Location position = center.clone().add(right.clone().multiply(cursor + piece.width / 2.0));
            String key = "xybh_combo_" + attacker.getUniqueId().toString().substring(0, 8) + "_" + id + "_" + i;
            if (bridge.show(viewer, key, position, piece.path, piece.width, piece.height)) {
                display.textures.add(new TextureRef(viewer.getUniqueId(), key));
            }
            cursor += piece.width + settings.getSpacing();
        }
    }

    private void tick() {
        Iterator<Map.Entry<UUID, ActiveDisplay>> iterator = active.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ActiveDisplay> entry = iterator.next();
            if (--entry.getValue().remaining <= 0) {
                removeTextures(entry.getValue());
                iterator.remove();
            }
        }
    }

    public void reload() {
        clear();
        bridge.discover();
    }

    public void clear() {
        for (ActiveDisplay display : active.values()) removeTextures(display);
        active.clear();
    }

    public void remove(UUID attacker) {
        ActiveDisplay display = active.remove(attacker);
        if (display != null) removeTextures(display);
    }

    public void shutdown() {
        clear();
        if (cleanupTask != null) cleanupTask.cancel();
    }

    public boolean isAvailable() { return bridge.isAvailable(); }
    public int size() { return active.size(); }

    private void removeTextures(ActiveDisplay display) {
        for (TextureRef texture : display.textures) {
            bridge.remove(plugin.getServer().getPlayer(texture.viewer), texture.key);
        }
    }

    private static final class Piece {
        private final String path;
        private final float width;
        private final float height;
        private Piece(String path, float width, float height) {
            this.path = path;
            this.width = width;
            this.height = height;
        }
    }

    private static final class ActiveDisplay {
        private final List<TextureRef> textures = new ArrayList<>();
        private int remaining;
        private ActiveDisplay(int remaining) { this.remaining = remaining; }
    }

    private static final class TextureRef {
        private final UUID viewer;
        private final String key;
        private TextureRef(UUID viewer, String key) {
            this.viewer = viewer;
            this.key = key;
        }
    }
}

