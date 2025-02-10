package com.github.telvarost.inventorytweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ModHelper implements ModInitializer {
    public static boolean isCratesModLoaded = false;

    @Override
    public void onInitialize() {
        isCratesModLoaded = FabricLoader.getInstance().isModLoaded("crate");
    }

    public static int canItemFitInSlot(ItemStack itemsToFit, Slot slotToCheck) {
        ItemStack dispenserSlotStack = slotToCheck.getStack();

        if (null == dispenserSlotStack) {
            /** - Slot is open */
            return 1;
        } else if (itemsToFit.isItemEqual(dispenserSlotStack)) {
            if (dispenserSlotStack.count == dispenserSlotStack.getMaxCount()) {
                /** - Slot is taken */
                return -1;
            } else {
                /** - Slot is partially empty and item matches */
                return 0;
            }
        }

        /** - Slot is taken */
        return -1;
    }
}
