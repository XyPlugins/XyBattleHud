package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class MoneySettings {
    private final boolean enabled;
    private final boolean mythicMobsEnabled;
    private final String providerPlugin;
    private final String displayName;
    private final String iconPath;
    private final int dedupeMillis;

    private MoneySettings(FileConfiguration config) {
        enabled = config.getBoolean("pickup.money.enabled", true);
        mythicMobsEnabled = config.getBoolean("pickup.money.mythicmobs-enabled", true);
        providerPlugin = nonEmpty(config.getString("pickup.money.provider-plugin"), "MythicMobs");
        displayName = nonEmpty(config.getString("pickup.money.display-name"), "金币");
        iconPath = nonEmpty(config.getString("pickup.money.icon"),
                "战斗视图/属性图标/金币图标.png");
        dedupeMillis = clamp(config.getInt("pickup.money.dedupe-millis", 250), 0, 5000);
    }

    static MoneySettings load(FileConfiguration config) {
        return new MoneySettings(config);
    }

    private static String nonEmpty(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return value.trim();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isEnabled() { return enabled; }
    public boolean isMythicMobsEnabled() { return mythicMobsEnabled; }
    public String getProviderPlugin() { return providerPlugin; }
    public String getDisplayName() { return displayName; }
    public String getIconPath() { return iconPath; }
    public int getDedupeMillis() { return dedupeMillis; }
}
