package org.xyplugin.xybattlehud.damage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DamageType {
    private final String id;
    private final int priority;
    private final boolean vanillaCritical;
    private final List<String> triggers;
    private final List<String> attributes;
    private final String color;
    private final String prefix;
    private final String suffix;
    private final List<String> digits;
    private final Map<Character, String> symbols;

    public DamageType(String id, int priority, boolean vanillaCritical, List<String> triggers,
                      List<String> attributes, String color, String prefix, String suffix,
                      List<String> digits, Map<Character, String> symbols) {
        this.id = id;
        this.priority = priority;
        this.vanillaCritical = vanillaCritical;
        this.triggers = Collections.unmodifiableList(triggers);
        this.attributes = Collections.unmodifiableList(attributes);
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.digits = Collections.unmodifiableList(digits);
        this.symbols = Collections.unmodifiableMap(symbols);
    }

    public String getId() { return id; }
    public int getPriority() { return priority; }
    public boolean isVanillaCritical() { return vanillaCritical; }
    public List<String> getTriggers() { return triggers; }
    public List<String> getAttributes() { return attributes; }
    public String getColor() { return color; }
    public String getPrefix() { return prefix; }
    public String getSuffix() { return suffix; }
    public List<String> getDigits() { return digits; }
    public Map<Character, String> getSymbols() { return symbols; }
}

