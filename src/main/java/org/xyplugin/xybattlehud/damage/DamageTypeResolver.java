package org.xyplugin.xybattlehud.damage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.xyplugin.xybattlehud.attribute.AttributeReader;
import org.xyplugin.xybattlehud.config.PluginSettings;

import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

public final class DamageTypeResolver {
    private static final String TYPE_METADATA = "xybattlehud.damage-type";
    private final PluginSettings settings;
    private final AttributeReader attributes;

    public DamageTypeResolver(PluginSettings settings, AttributeReader attributes) {
        this.settings = settings;
        this.attributes = attributes;
    }

    public DamageType resolve(LivingEntity attacker, LivingEntity target, String apMessage, boolean directAttack) {
        DamageType metadata = metadataType(attacker, target);
        if (metadata != null) return metadata;

        String message = apMessage == null ? "" : apMessage.toLowerCase(Locale.ROOT);
        for (DamageType type : settings.getOrderedTypes()) {
            for (String trigger : type.getTriggers()) {
                if (!trigger.isEmpty() && message.contains(trigger)) return type;
            }
        }
        if (directAttack && attacker instanceof Player && isVanillaCritical((Player) attacker)) {
            for (DamageType type : settings.getOrderedTypes()) if (type.isVanillaCritical()) return type;
        }
        if (attributes.isAvailable()) {
            for (DamageType type : settings.getOrderedTypes()) {
                for (String attribute : type.getAttributes()) {
                    OptionalDouble value = attributes.getValue(attacker, attribute);
                    if (value.isPresent() && value.getAsDouble() > settings.getAttributeThreshold()) return type;
                }
            }
        }
        return settings.getDefaultType();
    }

    private DamageType metadataType(LivingEntity attacker, LivingEntity target) {
        DamageType type = readMetadata(attacker.getMetadata(TYPE_METADATA));
        return type == null ? readMetadata(target.getMetadata(TYPE_METADATA)) : type;
    }

    private DamageType readMetadata(List<MetadataValue> values) {
        for (MetadataValue value : values) {
            DamageType type = settings.getTypes().get(value.asString());
            if (type != null) return type;
        }
        return null;
    }

    private boolean isVanillaCritical(Player player) {
        return player.getFallDistance() > 0.0F
                && !player.isOnGround()
                && !player.isInsideVehicle()
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && !player.isSprinting();
    }
}

