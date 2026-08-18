package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class PickupAnimationSettings {
    private final int durationMillis;
    private final int fadeInMillis;
    private final int fadeOutMillis;
    private final int maxEntries;
    private final int stackSpacing;
    private final int slidePixels;
    private final double slideSpeed;
    private final double stackMoveSpeed;

    private PickupAnimationSettings(FileConfiguration config) {
        durationMillis = clamp(config.getInt("pickup.animation.duration-millis", 3000), 200, 30000);
        fadeInMillis = clamp(config.getInt("pickup.animation.fade-in-millis", 0), 0, durationMillis);
        fadeOutMillis = clamp(config.getInt("pickup.animation.fade-out-millis", 600), 0, durationMillis);
        maxEntries = clamp(config.getInt("pickup.animation.max-entries", 5), 1, 20);
        stackSpacing = clamp(config.getInt("pickup.animation.stack-spacing", 25), 1, 200);
        slidePixels = clamp(config.getInt("pickup.animation.slide-pixels", 50), 0, 500);
        slideSpeed = clamp(config.getDouble("pickup.animation.slide-speed", 0.08), 0.01, 1.0);
        stackMoveSpeed = clamp(config.getDouble("pickup.animation.stack-move-speed", 0.35), 0.01, 1.0);
    }

    static PickupAnimationSettings load(FileConfiguration config) {
        return new PickupAnimationSettings(config);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public int getDurationMillis() {
        return durationMillis;
    }

    public int getFadeInMillis() {
        return fadeInMillis;
    }

    public int getFadeOutMillis() {
        return fadeOutMillis;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public int getStackSpacing() {
        return stackSpacing;
    }

    public int getSlidePixels() {
        return slidePixels;
    }

    public double getSlideSpeed() {
        return slideSpeed;
    }

    public double getStackMoveSpeed() {
        return stackMoveSpeed;
    }
}
