package org.xyplugin.xybattlehud.attribute;

import org.bukkit.entity.LivingEntity;

import java.util.OptionalDouble;

public interface AttributeReader {
    OptionalDouble getValue(LivingEntity entity, String attribute);
    boolean isAvailable();
    String getName();
}

