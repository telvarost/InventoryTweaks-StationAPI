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

    public static boolean isItemInSlot(ItemStack itemStack, Slot slotToCheck) {
        ItemStack slotStack = slotToCheck.getStack();

        if (null == slotStack) {
            /** - Slot does not have item */
            return false;
        } else if (itemStack.isItemEqual(slotStack)) {
            return true;
        }

        /** - Slot does not have item */
        return false;
    }

    public static int canItemFitInSlot(ItemStack itemStack, Slot slotToCheck) {
        ItemStack slotStack = slotToCheck.getStack();

        if (null == slotStack) {
            /** - Slot is open */
            return 1;
        } else if (itemStack.isItemEqual(slotStack)) {
            if (slotStack.count == slotStack.getMaxCount()) {
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
