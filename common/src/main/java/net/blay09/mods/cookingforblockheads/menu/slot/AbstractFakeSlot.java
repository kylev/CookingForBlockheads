package net.blay09.mods.cookingforblockheads.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.NonInteractiveResultSlot;

public abstract class AbstractFakeSlot extends NonInteractiveResultSlot {
    public AbstractFakeSlot(Container container, int slotId, int x, int y) {
        super(container, slotId, x, y);
    }
}
