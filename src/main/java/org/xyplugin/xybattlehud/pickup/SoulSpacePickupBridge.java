package org.xyplugin.xybattlehud.pickup;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

import java.lang.reflect.Method;

public final class SoulSpacePickupBridge implements Listener {
    private final XyBattleHudPlugin plugin;
    private boolean available;

    public SoulSpacePickupBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        HandlerList.unregisterAll(this);
        available = false;
        if (!plugin.getSettings().getPickup().isEnabled()) return;
        Plugin soulSpace = plugin.getServer().getPluginManager().getPlugin("XySoulSpace");
        if (soulSpace == null || !soulSpace.isEnabled()) return;
        boolean registered = false;
        if (plugin.getSettings().getPickup().isSoulSpaceEnabled()) {
            registered |= registerEvent(soulSpace,
                    "org.xyplugin.xysoulspace.api.XySoulSpaceItemDepositEvent", this::onDeposit);
        }
        registered |= registerEvent(soulSpace,
                "org.xyplugin.xysoulspace.api.XySoulSpaceItemInventoryDeliveryEvent",
                this::onInventoryDelivery);
        available = registered;
    }

    private boolean registerEvent(Plugin soulSpace, String className, EventExecutor executor) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(
                    className, true,
                    soulSpace.getClass().getClassLoader()).asSubclass(Event.class);
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR,
                    executor, plugin, true);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        } catch (Throwable failure) {
            if (plugin.isDebugEnabled()) {
                plugin.getLogger().warning("未接入 XySoulSpace 拾取事件 " + className + ": "
                        + failure.getMessage());
            }
            return false;
        }
    }

    private void onDeposit(Listener ignored, Event event) {
        Object source = invoke(event, "getSource");
        if (source == null || !"pickup".equalsIgnoreCase(String.valueOf(source))) return;
        Object player = invoke(event, "getPlayer");
        Object item = invoke(event, "getItem");
        if (!(player instanceof Player) || !(item instanceof ItemStack)) return;
        ItemStack stack = (ItemStack) item;
        plugin.getPickupDisplays().showSoulSpace((Player) player, stack, stack.getAmount());
    }

    private void onInventoryDelivery(Listener ignored, Event event) {
        Object player = invoke(event, "getPlayer");
        Object item = invoke(event, "getItem");
        if (!(player instanceof Player) || !(item instanceof ItemStack)) return;
        ItemStack stack = (ItemStack) item;
        plugin.getPickupDisplays().show((Player) player, stack, stack.getAmount());
    }

    private Object invoke(Object target, String name) {
        try {
            Method method = target.getClass().getMethod(name);
            return method.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean isAvailable() {
        return available;
    }
}
