package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class ComboSettings {
    private final boolean enabled;
    private final int timeoutTicks;
    private final int displayFrom;
    private final int maxCount;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final float numberWidth;
    private final float numberHeight;
    private final float labelWidth;
    private final float labelHeight;
    private final double spacing;
    private final String normalDigitsFolder;
    private final String criticalDigitsFolder;
    private final String normalLabel;
    private final String criticalLabel;

    private ComboSettings(FileConfiguration config) {
        enabled = config.getBoolean("combo.enabled", true);
        timeoutTicks = Math.max(1, config.getInt("combo.timeout-ticks", 40));
        displayFrom = Math.min(999, Math.max(1, config.getInt("combo.display-from", 2)));
        maxCount = Math.min(999, Math.max(displayFrom, config.getInt("combo.max-count", 999)));
        offsetX = config.getDouble("combo.position.x", 0.65);
        offsetY = config.getDouble("combo.position.y", 0.2);
        offsetZ = config.getDouble("combo.position.z", 0.0);
        numberWidth = positive(config.getDouble("combo.size.number-width", 0.10), 0.10);
        numberHeight = positive(config.getDouble("combo.size.number-height", 0.14), 0.14);
        labelWidth = positive(config.getDouble("combo.size.label-width", 0.22), 0.22);
        labelHeight = positive(config.getDouble("combo.size.label-height", 0.12), 0.12);
        spacing = Math.max(0.0, config.getDouble("combo.size.spacing", 0.01));
        normalDigitsFolder = trimSlash(config.getString("combo.images.normal-digits-folder",
                "战斗视图/艾尔字体/普通伤害"));
        criticalDigitsFolder = trimSlash(config.getString("combo.images.critical-digits-folder",
                "战斗视图/艾尔字体/暴击伤害"));
        normalLabel = config.getString("combo.images.normal-label",
                "战斗视图/伤害字体/连击数_1.png");
        criticalLabel = config.getString("combo.images.critical-label",
                "战斗视图/伤害字体/连击数_2.png");
    }

    static ComboSettings load(FileConfiguration config) {
        return new ComboSettings(config);
    }

    private static float positive(double value, double fallback) {
        return (float) (value > 0.0 ? value : fallback);
    }

    private static String trimSlash(String path) {
        if (path == null) return "";
        while (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public boolean isEnabled() { return enabled; }
    public int getTimeoutTicks() { return timeoutTicks; }
    public int getDisplayFrom() { return displayFrom; }
    public int getMaxCount() { return maxCount; }
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getOffsetZ() { return offsetZ; }
    public float getNumberWidth() { return numberWidth; }
    public float getNumberHeight() { return numberHeight; }
    public float getLabelWidth() { return labelWidth; }
    public float getLabelHeight() { return labelHeight; }
    public double getSpacing() { return spacing; }
    public String getNormalDigitsFolder() { return normalDigitsFolder; }
    public String getCriticalDigitsFolder() { return criticalDigitsFolder; }
    public String getNormalLabel() { return normalLabel; }
    public String getCriticalLabel() { return criticalLabel; }
}
