package org.xyplugin.xybattlehud.pickup;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class PickupListenerTest {
    @Test
    public void calculatesFullPickupAmount() {
        assertEquals(16, PickupAmounts.pickedAmount(16, 0));
    }

    @Test
    public void calculatesPartialPickupAmount() {
        assertEquals(12, PickupAmounts.pickedAmount(64, 52));
    }

    @Test
    public void clampsInvalidRemaining() {
        assertEquals(0, PickupAmounts.pickedAmount(8, 20));
        assertEquals(8, PickupAmounts.pickedAmount(8, -1));
    }
}
