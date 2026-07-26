package org.xyplugin.xybattlehud.attribute;

import org.bukkit.entity.LivingEntity;
import org.xyplugin.xycore.api.XyCore;
import org.xyplugin.xycore.api.attribute.AttributeService;
import org.xyplugin.xycore.api.attribute.AttributeValueMode;

import java.util.OptionalDouble;

final class XyCoreAttributeReader implements AttributeReader {
    private final AttributeService service;

    XyCoreAttributeReader() {
        service = XyCore.get().getAttributes();
    }

    @Override
    public OptionalDouble getValue(LivingEntity entity, String attribute) {
        return service.getValue(entity, attribute, AttributeValueMode.MAX);
    }

    @Override
    public boolean isAvailable() {
        return service.isAvailable();
    }

    @Override
    public String getName() {
        return "XyCore/" + service.getProviderName();
    }
}

