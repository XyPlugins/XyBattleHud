package org.xyplugin.xybattlehud.damage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DamageNumberFormatterTest {
    @Test
    public void formatsIntegerByDefault() {
        assertEquals("1235", DamageNumberFormatter.format(1234.6, 0, ""));
    }

    @Test
    public void formatsDecimalsAndGrouping() {
        assertEquals("1,234.50", DamageNumberFormatter.format(1234.5, 2, ","));
    }
}

