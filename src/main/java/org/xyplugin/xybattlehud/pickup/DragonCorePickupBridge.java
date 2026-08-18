package org.xyplugin.xybattlehud.pickup;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.PickupAnimationSettings;

import java.lang.reflect.Method;

final class DragonCorePickupBridge {
    private final XyBattleHudPlugin plugin;
    private Method sendOpenHud;
    private Method putClientSlotItem;
    private Method sendRunFunction;

    DragonCorePickupBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        discover();
    }

    void discover() {
        sendOpenHud = null;
        putClientSlotItem = null;
        sendRunFunction = null;
        Plugin dragonCore = plugin.getServer().getPluginManager().getPlugin("DragonCore");
        if (dragonCore == null || !dragonCore.isEnabled()) return;
        try {
            Class<?> sender = Class.forName("eos.moe.dragoncore.network.PacketSender", true,
                    dragonCore.getClass().getClassLoader());
            sendOpenHud = sender.getMethod("sendOpenHud", Player.class, String.class);
            putClientSlotItem = sender.getMethod("putClientSlotItem", Player.class, String.class, ItemStack.class);
            sendRunFunction = sender.getMethod("sendRunFunction", Player.class, String.class, String.class, boolean.class);
        } catch (Exception failure) {
            plugin.getLogger().warning("DragonCore 拾取 HUD 接口不可用，拾取视图已停用: " + failure.getMessage());
        }
    }

    boolean isAvailable() {
        return sendOpenHud != null && putClientSlotItem != null && sendRunFunction != null;
    }

    boolean show(Player player, String hudName, String functionName, String cacheKey, String token,
                 ItemStack item, int amount, String source, PickupAnimationSettings animation) {
        if (!isAvailable() || player == null || !player.isOnline() || item == null) return false;
        try {
            putClientSlotItem.invoke(null, player, cacheKey, item);
            sendOpenHud.invoke(null, player, hudName);
            String function = createFunction(functionName, token, amount, source, "", "", animation);
            sendRunFunction.invoke(null, player, hudName, function, false);
            return true;
        } catch (Exception failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("发送拾取视图失败: " + failure.getMessage());
            return false;
        }
    }

    boolean showExperience(Player player, String hudName, String functionName, String token,
                           long amount, String displayName, String iconPath,
                           PickupAnimationSettings animation) {
        if (!isAvailable() || player == null || !player.isOnline()) return false;
        try {
            sendOpenHud.invoke(null, player, hudName);
            String function = createFunction(functionName, token, amount, "experience",
                    displayName, iconPath, animation);
            sendRunFunction.invoke(null, player, hudName, function, false);
            return true;
        } catch (Exception failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("发送经验拾取视图失败: " + failure.getMessage());
            return false;
        }
    }

    private String createFunction(String functionName, String token, long amount, String source,
                                  String displayName, String iconPath, PickupAnimationSettings animation) {
        return "方法.执行方法('" + escape(functionName) + "','"
                + escape(token) + "','" + Math.max(1L, amount) + "','"
                + escape(source) + "','" + escape(displayName) + "','"
                + escape(iconPath) + "','"
                + animation.getDurationMillis() + "','"
                + animation.getFadeInMillis() + "','"
                + animation.getFadeOutMillis() + "','"
                + animation.getMaxEntries() + "','"
                + animation.getStackSpacing() + "','"
                + animation.getSlidePixels() + "','"
                + animation.getSlideSpeed() + "','"
                + animation.getStackMoveSpeed() + "');";
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
