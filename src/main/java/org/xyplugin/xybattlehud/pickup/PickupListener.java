package org.xyplugin.xybattlehud.pickup;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

public final class PickupListener implements Listener {
    private final XyBattleHudPlugin plugin;

    public PickupListener(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent event) {
        if (!plugin.getSettings().getPickup().isEnabled()) return;
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (player == null || item == null) return;
        ItemStack stack = item.getItemStack();
        if (!valid(stack)) return;
        int amount = PickupAmounts.pickedAmount(stack.getAmount(), event.getRemaining());
        if (amount <= 0) return;
        plugin.getPickupDisplays().show(player, stack, amount);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPickupDisplays().forget(event.getPlayer());
    }

    private boolean valid(ItemStack stack) {
        return stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0;
    }
}
