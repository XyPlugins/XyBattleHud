package org.xyplugin.xybattlehud.util;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessagePrefix {
    private MessagePrefix() {
    }

    public static String resolve(JavaPlugin plugin) {
        return resolvePlayer(plugin);
    }

    public static String resolveLocal(JavaPlugin plugin) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&7[&bXyBattleHud&7]&r ");
        return ChatColor.translateAlternateColorCodes('&', prefix == null ? "" : prefix);
    }

    public static String resolvePlayer(JavaPlugin plugin) {
        String corePrefix = xyCorePrefix();
        String prefix = corePrefix == null
                ? plugin.getConfig().getString("messages.prefix", "&7[&bXyBattleHud&7]&r ")
                : corePrefix;
        return ChatColor.translateAlternateColorCodes('&', prefix == null ? "" : prefix);
    }

    private static String xyCorePrefix() {
        Plugin core = Bukkit.getPluginManager().getPlugin("XyCore");
        if (core == null || !core.isEnabled()) return null;
        try {
            ClassLoader loader = core.getClass().getClassLoader();
            Class<?> entry = Class.forName("org.xyplugin.xycore.api.XyCore", true, loader);
            Object api = entry.getMethod("get").invoke(null);
            Method method = api.getClass().getMethod("getMessagePrefix");
            Object prefix = method.invoke(api);
            return prefix == null ? "" : String.valueOf(prefix);
        } catch (Exception ignored) {
            return null;
        }
    }
}
