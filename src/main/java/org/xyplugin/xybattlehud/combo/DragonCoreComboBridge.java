package org.xyplugin.xybattlehud.combo;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.config.ComboSettings;

import java.lang.reflect.Method;

final class DragonCoreComboBridge {
    private final XyBattleHudPlugin plugin;
    private Method sendOpenHud;
    private Method sendRunFunction;

    DragonCoreComboBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        discover();
    }

    void discover() {
        sendOpenHud = null;
        sendRunFunction = null;
        Plugin dragonCore = plugin.getServer().getPluginManager().getPlugin("DragonCore");
        if (dragonCore == null || !dragonCore.isEnabled()) return;
        try {
            Class<?> sender = Class.forName("eos.moe.dragoncore.network.PacketSender", true,
                    dragonCore.getClass().getClassLoader());
            sendOpenHud = sender.getMethod("sendOpenHud", Player.class, String.class);
            sendRunFunction = sender.getMethod("sendRunFunction", Player.class, String.class, String.class, boolean.class);
        } catch (Exception failure) {
            plugin.getLogger().warning("DragonCore 连击 HUD 接口不可用，连击显示已停用: " + failure.getMessage());
        }
    }

    boolean isAvailable() {
        return sendOpenHud != null && sendRunFunction != null;
    }

    boolean show(Player player, ComboSettings settings, int count, boolean critical) {
        if (!isAvailable() || player == null || !player.isOnline()) return false;
        try {
            sendOpenHud.invoke(null, player, settings.getHudName());
            int safeCount = Math.max(1, Math.min(999, count));
            int durationMillis = Math.max(50, settings.getTimeoutTicks() * 50);
            String function = "方法.执行方法('" + escape(settings.getUpdateFunctionName()) + "','"
                    + safeCount + "','" + (critical ? "true" : "false") + "','" + durationMillis + "');";
            sendRunFunction.invoke(null, player, settings.getHudName(), function, false);
            return true;
        } catch (Exception failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("发送连击 HUD 失败: " + failure.getMessage());
            return false;
        }
    }

    void clear(Player player, String hudName, String functionName) {
        if (!isAvailable() || player == null || !player.isOnline()) return;
        try {
            String function = "方法.执行方法('" + escape(functionName) + "');";
            sendRunFunction.invoke(null, player, hudName, function, false);
        } catch (Exception failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("清除连击 HUD 失败: " + failure.getMessage());
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
