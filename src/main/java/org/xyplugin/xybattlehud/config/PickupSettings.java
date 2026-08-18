package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class PickupSettings {
    private final boolean enabled;
    private final boolean soulSpaceEnabled;
    private final boolean soulSpaceFrameEnabled;
    private final ExperienceSettings experience;
    private final PickupAnimationSettings animation;
    private final String hudName;
    private final String functionName;
    private final String cachePrefix;
    private final int rightOffset;
    private final int bottomOffset;

    private PickupSettings(FileConfiguration config) {
        enabled = config.getBoolean("pickup.enabled", true);
        soulSpaceEnabled = config.getBoolean("pickup.soul-space-enabled", true);
        soulSpaceFrameEnabled = config.getBoolean("pickup.soul-space-frame-enabled", true);
        experience = ExperienceSettings.load(config);
        animation = PickupAnimationSettings.load(config);
        hudName = nonEmpty(config.getString("pickup.hud-name"), "XyBattleHud拾取视图");
        functionName = nonEmpty(config.getString("pickup.function-name"), "创建拾取");
        cachePrefix = nonEmpty(config.getString("pickup.cache-prefix"), "xybh_pickup_item_");
        rightOffset = clamp(config.getInt("pickup.position.right", 8), 1, 2000);
        bottomOffset = clamp(config.getInt("pickup.position.bottom", 74), 1, 2000);
    }

    static PickupSettings load(FileConfiguration config) {
        return new PickupSettings(config);
    }

    private static String nonEmpty(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return value.trim();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isEnabled() { return enabled; }
    public boolean isSoulSpaceEnabled() { return soulSpaceEnabled; }
    public boolean isSoulSpaceFrameEnabled() { return soulSpaceFrameEnabled; }
    public ExperienceSettings getExperience() { return experience; }
    public PickupAnimationSettings getAnimation() { return animation; }
    public String getHudName() { return hudName; }
    public String getFunctionName() { return functionName; }
    public String getCachePrefix() { return cachePrefix; }
    public int getRightOffset() { return rightOffset; }
    public int getBottomOffset() { return bottomOffset; }
}
