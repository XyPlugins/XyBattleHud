package org.xyplugin.xybattlehud.pickup;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.PickupSettings;

import java.util.concurrent.atomic.AtomicLong;

public final class PickupDisplayManager {
    private final XyBattleHudPlugin plugin;
    private final AtomicLong sequence = new AtomicLong();
    private DragonCorePickupBridge bridge;

    public PickupDisplayManager(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        bridge = new DragonCorePickupBridge(plugin);
    }

    public boolean show(Player player, ItemStack stack, int amount) {
        return show(player, stack, amount, "normal");
    }

    public boolean showSoulSpace(Player player, ItemStack stack, int amount) {
        PickupSettings settings = plugin.getSettings().getPickup();
        return show(player, stack, amount, settings.isSoulSpaceFrameEnabled() ? "soul" : "normal");
    }

    public boolean showExperience(Player player, long amount, String displayName, String iconPath) {
        PickupSettings settings = plugin.getSettings().getPickup();
        if (!settings.isEnabled() || !settings.getExperience().isEnabled()
                || !bridge.isAvailable() || player == null || !player.isOnline() || amount <= 0) {
            return false;
        }
        String token = player.getUniqueId().toString().substring(0, 8) + "_"
                + Long.toString(sequence.incrementAndGet(), 36);
        boolean sent = bridge.showExperience(player, settings.getHudName(), settings.getFunctionName(),
                token, amount, displayName, iconPath);
        if (plugin.isDebugEnabled() && sent) {
            plugin.getLogger().info("拾取视图(experience): " + player.getName()
                    + " +" + amount + " " + displayName);
        }
        return sent;
    }

    private boolean show(Player player, ItemStack stack, int amount, String source) {
        PickupSettings settings = plugin.getSettings().getPickup();
        if (!settings.isEnabled() || !bridge.isAvailable() || !valid(stack) || amount <= 0) return false;
        String token = player.getUniqueId().toString().substring(0, 8) + "_"
                + Long.toString(sequence.incrementAndGet(), 36);
        String cacheKey = settings.getCachePrefix() + token;
        ItemStack display = stack.clone();
        display.setAmount(Math.max(1, Math.min(display.getMaxStackSize(), amount)));
        boolean sent = bridge.show(player, settings.getHudName(), settings.getFunctionName(),
                cacheKey, token, display, amount, source);
        if (plugin.isDebugEnabled() && sent) {
            plugin.getLogger().info("拾取视图(" + source + "): " + player.getName()
                    + " x" + amount + " " + stack.getType());
        }
        return sent;
    }

    public void reload() {
        bridge.discover();
    }

    public boolean isAvailable() {
        return bridge.isAvailable();
    }

    private boolean valid(ItemStack stack) {
        return stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0;
    }
}
