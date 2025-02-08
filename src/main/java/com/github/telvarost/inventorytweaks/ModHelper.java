package com.github.telvarost.inventorytweaks;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;

public class ModHelper {

    public static ItemStack getResultFor(ItemStack slotStack) {
        return SmeltingRegistry.getResultFor(slotStack);
    }

    public static int getFuelTime(ItemStack slotStack) {
        return FuelRegistry.getFuelTime(slotStack);
    }
}
