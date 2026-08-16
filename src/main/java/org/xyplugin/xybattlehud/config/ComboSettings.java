package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class ComboSettings {
    private final boolean enabled;
    private final int timeoutTicks;
    private final int displayFrom;
    private final int maxCount;
    private final String hudName;
    private final String updateFunctionName;
    private final String clearFunctionName;

    private ComboSettings(FileConfiguration config) {
        enabled = config.getBoolean("combo.enabled", true);
        timeoutTicks = Math.max(1, config.getInt("combo.timeout-ticks", 40));
        displayFrom = Math.min(999, Math.max(1, config.getInt("combo.display-from", 2)));
        maxCount = Math.min(999, Math.max(displayFrom, config.getInt("combo.max-count", 999)));
        hudName = nonEmpty(config.getString("combo.hud-name"), "XyBattleHud连击视图");
        updateFunctionName = nonEmpty(config.getString("combo.update-function"), "更新连击");
        clearFunctionName = nonEmpty(config.getString("combo.clear-function"), "清除连击");
    }

    static ComboSettings load(FileConfiguration config) {
        return new ComboSettings(config);
    }

    private static String nonEmpty(String value, String fallback) {
        if (value == null) return fallback;
        value = value.trim();
        return value.isEmpty() ? fallback : value;
    }

    public boolean isEnabled() { return enabled; }
    public int getTimeoutTicks() { return timeoutTicks; }
    public int getDisplayFrom() { return displayFrom; }
    public int getMaxCount() { return maxCount; }
    public String getHudName() { return hudName; }
    public String getUpdateFunctionName() { return updateFunctionName; }
    public String getClearFunctionName() { return clearFunctionName; }
}
