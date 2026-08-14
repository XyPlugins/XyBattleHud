package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class PickupSettings {
    private final boolean enabled;
    private final boolean soulSpaceEnabled;
    private final String hudName;
    private final String functionName;
    private final String cachePrefix;

    private PickupSettings(FileConfiguration config) {
        enabled = config.getBoolean("pickup.enabled", true);
        soulSpaceEnabled = config.getBoolean("pickup.soul-space-enabled", true);
        hudName = nonEmpty(config.getString("pickup.hud-name"), "XyBattleHud拾取视图");
        functionName = nonEmpty(config.getString("pickup.function-name"), "创建拾取");
        cachePrefix = nonEmpty(config.getString("pickup.cache-prefix"), "xybh_pickup_item_");
    }

    static PickupSettings load(FileConfiguration config) {
        return new PickupSettings(config);
    }

    private static String nonEmpty(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return value.trim();
    }

    public boolean isEnabled() { return enabled; }
    public boolean isSoulSpaceEnabled() { return soulSpaceEnabled; }
    public String getHudName() { return hudName; }
    public String getFunctionName() { return functionName; }
    public String getCachePrefix() { return cachePrefix; }
}
