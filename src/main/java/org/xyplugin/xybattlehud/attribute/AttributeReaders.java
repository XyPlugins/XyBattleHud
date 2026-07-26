package org.xyplugin.xybattlehud.attribute;

import org.bukkit.plugin.Plugin;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

import java.util.OptionalDouble;

public final class AttributeReaders {
    private AttributeReaders() {
    }

    public static AttributeReader discover(XyBattleHudPlugin plugin, boolean preferXyCore) {
        if (preferXyCore && enabled(plugin, "XyCore")) {
            try {
                AttributeReader reader = new XyCoreAttributeReader();
                if (reader.isAvailable()) return reader;
            } catch (Throwable failure) {
                plugin.getLogger().warning("XyCore 属性接口不可用，尝试直连 AttributePlus: " + failure.getMessage());
            }
        }
        Plugin attributePlus = plugin.getServer().getPluginManager().getPlugin("AttributePlus");
        if (attributePlus != null && attributePlus.isEnabled()) {
            try { return new DirectAttributePlusReader(attributePlus); }
            catch (Exception failure) {
                plugin.getLogger().warning("AttributePlus 属性接口不可用: " + failure.getMessage());
            }
        }
        return new AttributeReader() {
            public OptionalDouble getValue(org.bukkit.entity.LivingEntity entity, String attribute) {
                return OptionalDouble.empty();
            }
            public boolean isAvailable() { return false; }
            public String getName() { return "无"; }
        };
    }

    private static boolean enabled(XyBattleHudPlugin plugin, String name) {
        Plugin dependency = plugin.getServer().getPluginManager().getPlugin(name);
        return dependency != null && dependency.isEnabled();
    }
}

