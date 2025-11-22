package com.github.telvarost.inventorytweaks;

import com.matthewperiut.crate.gui.GuiCrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.input.Keyboard;

public class ModHelperCrates {

    public static boolean isCratesScreen(Screen screen) {
        return (screen instanceof GuiCrate);
    }

    public static boolean inventoryTweaks_handleShiftClickIntoCrate(int button, Slot clickedSlot, Minecraft minecraft) {
        boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
        if (isShiftKeyDown) {

            if (null != clickedSlot && clickedSlot.hasStack()) {
                GuiCrate crateScreen = (GuiCrate) minecraft.currentScreen;
                int totalSlots = crateScreen.container.slots.size();
                //int numberOfContainerSlots = totalSlots - 36;

                if (clickedSlot.id < 36) {
                    ItemStack slotStack = clickedSlot.getStack();
                    int isSlotAvailable;
                    int dispenserSlotIndex;
                    boolean itemsShifted = false;

                    for (dispenserSlotIndex = (totalSlots - 12); dispenserSlotIndex < totalSlots; dispenserSlotIndex++) {
                        isSlotAvailable = ModHelper.canItemFitInSlot(slotStack, ((Slot) crateScreen.container.slots.get(dispenserSlotIndex)));

                        if (0 == isSlotAvailable) {
                            /** - Partially full item slot of matching item found. */
                            minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                            minecraft.interactionManager.clickSlot(crateScreen.container.syncId, ((Slot) crateScreen.container.slots.get(dispenserSlotIndex)).id, button, false, minecraft.player);
                            minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                            itemsShifted = true;
                            if (false == clickedSlot.hasStack()) {
                                break;
                            }
                        } else if (1 == isSlotAvailable) {
                            /** - Empty slot found! */
                            if (null != minecraft.player.inventory.getCursorStack()) {
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, ((Slot) crateScreen.container.slots.get(dispenserSlotIndex)).id, button, false, minecraft.player);
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                            } else {
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, ((Slot) crateScreen.container.slots.get(dispenserSlotIndex)).id, button, false, minecraft.player);
                            }
                            itemsShifted = true;
                            break;
                        }
                    }

                    if (itemsShifted) {
                        return true;
                    }
                } else {
                    ItemStack slotStack = clickedSlot.getStack();
                    int playerInventorySlotIndex;
                    int shiftToSlot;
                    int isSlotAvailable;
                    boolean itemsShifted = false;

                    /** - Shift item back into player inventory */
                    for (playerInventorySlotIndex = 0; playerInventorySlotIndex < (totalSlots - 12); playerInventorySlotIndex++) {
                        shiftToSlot = ((totalSlots - 12) - playerInventorySlotIndex) - 1;
                        isSlotAvailable = ModHelper.canItemFitInSlot(slotStack, ((Slot) crateScreen.container.slots.get(shiftToSlot)));

                        if (0 == isSlotAvailable) {
                            /** - Partially full item slot of matching item found. */
                            minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                            minecraft.interactionManager.clickSlot(crateScreen.container.syncId, ((Slot) crateScreen.container.slots.get(shiftToSlot)).id, button, false, minecraft.player);
                            minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                            itemsShifted = true;
                            if (false == clickedSlot.hasStack()) {
                                break;
                            }
                        } else if (1 == isSlotAvailable) {
                            /** - Empty slot found! */
                            if (null != minecraft.player.inventory.getCursorStack()) {
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, ((Slot) crateScreen.container.slots.get(shiftToSlot)).id, button, false, minecraft.player);
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                            } else {
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, clickedSlot.id, button, false, minecraft.player);
                                minecraft.interactionManager.clickSlot(crateScreen.container.syncId, ((Slot) crateScreen.container.slots.get(shiftToSlot)).id, button, false, minecraft.player);
                            }
                            itemsShifted = true;
                            break;
                        }
                    }

                    if (itemsShifted) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
