package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.DispenserScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DispenserScreenHandler.class)
public abstract class DispenserScreenHandlerMixin extends ScreenHandler {

    @Shadow private DispenserBlockEntity dispenserBlockEntity;

    @Override
    public ItemStack quickMove(int slot) {
        if (  (  (null != dispenserBlockEntity.world)
              && (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
              )
           || (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.MultiplayerShiftClickingItemsIntoDispensers)
        ) {
            ItemStack slotItemCopy = null;
            Slot clickedSlot = (Slot)this.slots.get(slot);

            if (clickedSlot != null && clickedSlot.hasStack()) {
                ItemStack slotItem = clickedSlot.getStack();
                slotItemCopy = slotItem.copy();
                if (slot < dispenserBlockEntity.size()) {
                    this.insertItem(slotItem, dispenserBlockEntity.size(), this.slots.size(), true);
                } else {
                    this.insertItem(slotItem, 0, dispenserBlockEntity.size(), false);
                }

                if (slotItem.count == 0) {
                    clickedSlot.setStack(null);
                } else {
                    clickedSlot.markDirty();
                }
            }

            return slotItemCopy;
        } else {
            return super.quickMove(slot);
        }
    }
}
