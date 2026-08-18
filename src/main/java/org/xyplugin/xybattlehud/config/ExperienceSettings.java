package org.xyplugin.xybattlehud.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class ExperienceSettings {
    private final boolean enabled;
    private final boolean akariLevelEnabled;
    private final String providerPlugin;
    private final String eventClassName;
    private final String memberVariable;
    private final String amountVariable;
    private final String levelGroupVariable;
    private final String sourceVariable;
    private final String levelGroup;
    private final List<String> sources;
    private final String displayName;
    private final String iconPath;
    private final int dedupeMillis;

    private ExperienceSettings(FileConfiguration config) {
        enabled = config.getBoolean("pickup.experience.enabled", true);
        akariLevelEnabled = config.getBoolean("pickup.experience.akari-level-enabled", true);
        providerPlugin = nonEmpty(config.getString("pickup.experience.provider-plugin"), "AkariLevel");
        eventClassName = nonEmpty(config.getString("pickup.experience.event-class"),
                "top.cpjinan.akarilevel.event.MemberExpChangeEvent");
        memberVariable = nonEmpty(config.getString("pickup.experience.player-variable"), "member");
        amountVariable = nonEmpty(config.getString("pickup.experience.amount-variable"), "expAmount");
        levelGroupVariable = nonEmpty(config.getString("pickup.experience.level-group-variable"), "levelGroup");
        sourceVariable = nonEmpty(config.getString("pickup.experience.source-variable"), "source");
        levelGroup = value(config.getString("pickup.experience.level-group"));
        sources = lower(config.getStringList("pickup.experience.sources"));
        displayName = nonEmpty(config.getString("pickup.experience.display-name"), "经验");
        iconPath = nonEmpty(config.getString("pickup.experience.icon"),
                "战斗视图/属性图标/经验加成图标.png");
        dedupeMillis = clamp(config.getInt("pickup.experience.dedupe-millis", 250), 0, 5000);
    }

    static ExperienceSettings load(FileConfiguration config) {
        return new ExperienceSettings(config);
    }

    public boolean acceptsLevelGroup(String value) {
        return levelGroup.isEmpty() || levelGroup.equalsIgnoreCase(value == null ? "" : value.trim());
    }

    public boolean acceptsSource(String value) {
        if (sources.isEmpty()) return true;
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return sources.contains(normalized);
    }

    private static List<String> lower(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                result.add(value.trim().toLowerCase(Locale.ROOT));
            }
        }
        return Collections.unmodifiableList(result);
    }

    private static String value(String value) {
        return value == null ? "" : value.trim();
    }

    private static String nonEmpty(String value, String fallback) {
        String result = value(value);
        return result.isEmpty() ? fallback : result;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAkariLevelEnabled() {
        return akariLevelEnabled;
    }

    public String getProviderPlugin() {
        return providerPlugin;
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public String getMemberVariable() {
        return memberVariable;
    }

    public String getAmountVariable() {
        return amountVariable;
    }

    public String getLevelGroupVariable() {
        return levelGroupVariable;
    }

    public String getSourceVariable() {
        return sourceVariable;
    }

    public String getLevelGroup() {
        return levelGroup;
    }

    public List<String> getSources() {
        return sources;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public int getDedupeMillis() {
        return dedupeMillis;
    }
}
