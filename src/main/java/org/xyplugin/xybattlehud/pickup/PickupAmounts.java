package org.xyplugin.xybattlehud.pickup;

final class PickupAmounts {
    private PickupAmounts() {
    }

    static int pickedAmount(int stackAmount, int remaining) {
        return Math.max(0, stackAmount - Math.max(0, remaining));
    }
}
