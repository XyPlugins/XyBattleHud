package org.xyplugin.xybattlehud.combo;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

import java.lang.reflect.Method;
import java.util.UUID;

final class DragonCoreTextureBridge {
    private final XyBattleHudPlugin plugin;
    private Method setTexture;
    private Method removeTexture;

    DragonCoreTextureBridge(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
        discover();
    }

    void discover() {
        setTexture = null;
        removeTexture = null;
        Plugin dragonCore = plugin.getServer().getPluginManager().getPlugin("DragonCore");
        if (dragonCore == null || !dragonCore.isEnabled()) return;
        try {
            Class<?> api = Class.forName("eos.moe.dragoncore.api.CoreAPI", true,
                    dragonCore.getClass().getClassLoader());
            setTexture = api.getMethod("setPlayerWorldTexture", Player.class, String.class, Location.class,
                    float.class, float.class, float.class, String.class, float.class, float.class, float.class,
                    boolean.class, boolean.class, UUID.class, boolean.class, float.class, float.class, float.class);
            removeTexture = api.getMethod("removePlayerWorldTexture", Player.class, String.class);
        } catch (Exception failure) {
            plugin.getLogger().warning("DragonCore 世界贴图接口不可用，连击显示已停用: " + failure.getMessage());
        }
    }

    boolean isAvailable() {
        return setTexture != null && removeTexture != null;
    }

    boolean show(Player viewer, String key, Location location, String path, float width, float height) {
        if (!isAvailable()) return false;
        try {
            setTexture.invoke(null, viewer, key, location, 0.0F, 0.0F, 0.0F, path,
                    width, height, 1.0F, true, false, null, false, 0.0F, 0.0F, 0.0F);
            return true;
        } catch (Exception failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("发送连击贴图失败: " + failure.getMessage());
            return false;
        }
    }

    void remove(Player viewer, String key) {
        if (!isAvailable() || viewer == null || !viewer.isOnline()) return;
        try {
            removeTexture.invoke(null, viewer, key);
        } catch (Exception failure) {
            if (plugin.isDebugEnabled()) plugin.getLogger().warning("移除连击贴图失败: " + failure.getMessage());
        }
    }
}

