package org.xyplugin.xybattlehud.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.xyplugin.xybattlehud.damage.DamageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PluginSettings {
    private final int durationTicks;
    private final double heightOffset;
    private final double floatSpeed;
    private final double randomOffset;
    private final int maxHolograms;
    private final int decimalPlaces;
    private final String thousandsSeparator;
    private final double minDamage;
    private final boolean ignoreSelfDamage;
    private final Set<String> ignoredWorlds;
    private final Set<EntityType> ignoredEntityTypes;
    private final boolean preferXyCore;
    private final double attributeThreshold;
    private final ComboSettings combo;
    private final Map<String, DamageType> types;
    private final List<DamageType> orderedTypes;
    private final DamageType defaultType;
    private final boolean debug;

    private PluginSettings(FileConfiguration config) {
        durationTicks = Math.max(1, config.getInt("display.duration-ticks", 40));
        heightOffset = config.getDouble("display.height-offset", 0.45);
        floatSpeed = config.getDouble("display.float-speed", 0.025);
        randomOffset = Math.max(0.0, config.getDouble("display.random-offset", 0.35));
        maxHolograms = Math.max(1, config.getInt("display.max-holograms", 100));
        decimalPlaces = Math.max(0, Math.min(6, config.getInt("number.decimal-places", 0)));
        thousandsSeparator = config.getString("number.thousands-separator", "");
        minDamage = Math.max(0.0, config.getDouble("number.min-damage", 0.1));
        ignoreSelfDamage = config.getBoolean("filter.ignore-self-damage", true);
        ignoredWorlds = new HashSet<>(config.getStringList("filter.ignored-worlds"));
        ignoredEntityTypes = parseEntityTypes(config.getStringList("filter.ignored-entity-types"));
        preferXyCore = config.getBoolean("attribute.prefer-xycore", true);
        attributeThreshold = config.getDouble("attribute.threshold", 0.0);
        combo = ComboSettings.load(config);
        debug = config.getBoolean("debug", false);

        Map<String, DamageType> loaded = loadTypes(config.getConfigurationSection("damage-types"));
        if (loaded.isEmpty()) throw new IllegalArgumentException("damage-types 至少需要一个类型");
        types = Collections.unmodifiableMap(loaded);
        orderedTypes = new ArrayList<>(loaded.values());
        orderedTypes.sort(Comparator.comparingInt(DamageType::getPriority).reversed());
        String defaultId = config.getString("default-type", "normal");
        defaultType = loaded.containsKey(defaultId) ? loaded.get(defaultId) : orderedTypes.get(orderedTypes.size() - 1);
    }

    public static PluginSettings load(FileConfiguration config) {
        return new PluginSettings(config);
    }

    private Map<String, DamageType> loadTypes(ConfigurationSection root) {
        Map<String, DamageType> loaded = new LinkedHashMap<>();
        if (root == null) return loaded;
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null || !section.getBoolean("enabled", true)) continue;
            List<String> digits = section.getStringList("digits");
            if (digits.size() != 10) throw new IllegalArgumentException("damage-types." + id + ".digits 必须正好有 10 项");
            Map<Character, String> symbols = new HashMap<>();
            ConfigurationSection symbolSection = section.getConfigurationSection("symbols");
            if (symbolSection != null) {
                for (String key : symbolSection.getKeys(false)) {
                    if (key.length() == 1) symbols.put(key.charAt(0), symbolSection.getString(key, key));
                }
            }
            loaded.put(id, new DamageType(id, section.getInt("priority", 0),
                    section.getBoolean("vanilla-critical", false), lower(section.getStringList("triggers")),
                    section.getStringList("attributes"), color(section.getString("color", "&f")),
                    color(section.getString("prefix", "")), color(section.getString("suffix", "")),
                    new ArrayList<>(digits), symbols));
        }
        return loaded;
    }

    private static List<String> lower(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) result.add(value.toLowerCase(LocaleHolder.ROOT));
        return result;
    }

    private static String color(String value) {
        return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value);
    }

    private static Set<EntityType> parseEntityTypes(List<String> names) {
        Set<EntityType> result = new HashSet<>();
        for (String name : names) {
            try { result.add(EntityType.valueOf(name.toUpperCase(LocaleHolder.ROOT))); }
            catch (IllegalArgumentException ignored) { }
        }
        return result;
    }

    private static final class LocaleHolder {
        private static final java.util.Locale ROOT = java.util.Locale.ROOT;
    }

    public int getDurationTicks() { return durationTicks; }
    public double getHeightOffset() { return heightOffset; }
    public double getFloatSpeed() { return floatSpeed; }
    public double getRandomOffset() { return randomOffset; }
    public int getMaxHolograms() { return maxHolograms; }
    public int getDecimalPlaces() { return decimalPlaces; }
    public String getThousandsSeparator() { return thousandsSeparator; }
    public double getMinDamage() { return minDamage; }
    public boolean isIgnoreSelfDamage() { return ignoreSelfDamage; }
    public Set<String> getIgnoredWorlds() { return ignoredWorlds; }
    public Set<EntityType> getIgnoredEntityTypes() { return ignoredEntityTypes; }
    public boolean isPreferXyCore() { return preferXyCore; }
    public double getAttributeThreshold() { return attributeThreshold; }
    public ComboSettings getCombo() { return combo; }
    public Map<String, DamageType> getTypes() { return types; }
    public List<DamageType> getOrderedTypes() { return orderedTypes; }
    public DamageType getDefaultType() { return defaultType; }
    public boolean isDebug() { return debug; }
}
