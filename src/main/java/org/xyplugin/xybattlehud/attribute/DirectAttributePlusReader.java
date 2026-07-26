package org.xyplugin.xybattlehud.attribute;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.OptionalDouble;

final class DirectAttributePlusReader implements AttributeReader {
    private final Method getAttrData;

    DirectAttributePlusReader(Plugin attributePlus) throws Exception {
        ClassLoader loader = attributePlus.getClass().getClassLoader();
        Class<?> api = Class.forName("org.serverct.ersha.api.AttributeAPI", true, loader);
        getAttrData = api.getMethod("getAttrData", LivingEntity.class);
    }

    @Override
    public OptionalDouble getValue(LivingEntity entity, String attribute) {
        try {
            Object data = getAttrData.invoke(null, entity);
            if (data == null) return OptionalDouble.empty();
            Method method = data.getClass().getMethod("getAttributeValue", String.class);
            Object value = method.invoke(data, attribute);
            if (value instanceof Number) return OptionalDouble.of(((Number) value).doubleValue());
            if (value != null && value.getClass().isArray()) {
                double maximum = Double.NEGATIVE_INFINITY;
                int size = Array.getLength(value);
                for (int i = 0; i < size; i++) {
                    Object item = Array.get(value, i);
                    if (item instanceof Number) maximum = Math.max(maximum, ((Number) item).doubleValue());
                }
                if (maximum != Double.NEGATIVE_INFINITY) return OptionalDouble.of(maximum);
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return OptionalDouble.empty();
    }

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public String getName() { return "AttributePlus-API"; }
}

