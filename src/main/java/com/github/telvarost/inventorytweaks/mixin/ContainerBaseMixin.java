package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

import static java.lang.Math.abs;

@Mixin(HandledScreen.class)
public abstract class ContainerBaseMixin extends Screen {

	@Shadow
	protected int backgroundWidth;

	@Shadow
	protected int backgroundHeight;

	@Shadow
	public net.minecraft.screen.ScreenHandler container;

	@Shadow
	protected abstract Slot getSlotAt(int x, int y);

	@Shadow
	protected abstract boolean isPointOverSlot(Slot slot, int x, int Y);

	@Unique private Slot slot;

	@Unique Slot lastRMBSlot = null;

	@Unique Slot lastLMBSlot = null;

	@Unique int lastRMBSlotId = -1;

	@Unique int lastLMBSlotId = -1;

	@Unique
	private ItemStack leftClickMouseTweaksPersistentStack = null;

	@Unique
	private ItemStack leftClickPersistentStack = null;

	@Unique
	private ItemStack rightClickPersistentStack = null;

	@Unique
	private boolean isLeftClickDragMouseTweaksStarted = false;

	@Unique
	private boolean isLeftClickDragStarted = false;

	@Unique
	private boolean isRightClickDragStarted = false;

	@Unique
	private final List<Slot> leftClickHoveredSlots = new ArrayList<>();

	@Unique final List<Slot> rightClickHoveredSlots = new ArrayList<>();

	@Unique Integer leftClickItemAmount;

	@Unique Integer rightClickItemAmount;

	@Unique final List<Integer> leftClickExistingAmount = new ArrayList<>();

	@Unique final List<Integer> rightClickExistingAmount = new ArrayList<>();

	@Unique List<Integer> leftClickAmountToFillPersistent = new ArrayList<>();

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	protected void inventoryTweaks_mouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
		isLeftClickDragMouseTweaksStarted = false;

		/** - Handle ctrl click crafting */
		if (Config.INVENTORY_TWEAKS_CONFIG.EnableCtrlClickCrafting) {
			if (inventoryTweaks_handleCtrlClickCrafting(mouseX, mouseY, button)) {
				/** - Handle if a button was clicked */
				super.mouseClicked(mouseX, mouseY, button);
				ci.cancel();
				return;
			}
		}

		/** - Handle right click crafting */
		if (Config.INVENTORY_TWEAKS_CONFIG.EnableRightClickCrafting) {
			if (inventoryTweaks_handleRightClickCrafting(mouseX, mouseY, button)) {
				/** - Handle if a button was clicked */
				super.mouseClicked(mouseX, mouseY, button);
				ci.cancel();
				return;
			}
		}

		/** - Handle shift click crafting */
		if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.EnableShiftClickCrafting) {
			if (inventoryTweaks_handleShiftClickCrafting(mouseX, mouseY, button)) {
				/** - Handle if a button was clicked */
				super.mouseClicked(mouseX, mouseY, button);
				ci.cancel();
				return;
			}
		}

		/** - Check if client is on a server */
		boolean isClientOnServer = minecraft.world.isRemote;

		/** - Right-click */
		if (button == 1) {
			boolean exitFunction = false;

			/** - Should click cancel Left-click + Drag */
			if (!inventoryTweaks_cancelLeftClickDrag(isClientOnServer)) {

				/** - Handle Right-click */
				if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.EnableRightClickDrag) {
					exitFunction = inventoryTweaks_handleRightClick(mouseX, mouseY);
				}
			} else {
				exitFunction = true;
			}

			if (exitFunction) {
				/** - Handle if a button was clicked */
				super.mouseClicked(mouseX, mouseY, button);
				ci.cancel();
				return;
			}
		}

		/** - Left-click */
		if (button == 0) {
			boolean exitFunction = false;

			/** - Should click cancel Right-click + Drag */
			if (!inventoryTweaks_cancelRightClickDrag(isClientOnServer)) {

				/** - Handle Left-click */
				ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
				Slot clickedSlot = this.getSlotAt(mouseX, mouseY);
				if (cursorStack != null) {
					if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.EnableLeftClickDrag) {
						exitFunction = inventoryTweaks_handleLeftClickWithItem(cursorStack, clickedSlot, isClientOnServer);
					}
				} else {
					exitFunction = inventoryTweaks_handleLeftClickWithoutItem(clickedSlot);
				}
			} else {
				exitFunction = true;
			}

			if (exitFunction) {
				/** - Handle if a button was clicked */
				super.mouseClicked(mouseX, mouseY, button);
				ci.cancel();
				return;
			}
		}
	}

	@Unique private boolean inventoryTweaks_handleCtrlClickCrafting(int mouseX, int mouseY, int button) {
		Slot slot = this.getSlotAt(mouseX, mouseY);

		if (slot instanceof CraftingResultSlot) {
			boolean isCtrlKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
			/** - Ctrl-click */
			if (true == isCtrlKeyDown && slot.hasStack()) {
				int maxStackSize = slot.getStack().getMaxCount();
				int numCrafted = 0;
				for (int craftingAttempts = 0; craftingAttempts < 256; craftingAttempts++) {
					if (slot.hasStack() && numCrafted < maxStackSize) {
						numCrafted += slot.getStack().count;
						this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, button, true, this.minecraft.player);
					} else {
						break;
					}
				}
				return true;
			}
		}

		return false;
	}

	@Unique private boolean inventoryTweaks_handleRightClickCrafting(int mouseX, int mouseY, int button) {
		Slot slot = this.getSlotAt(mouseX, mouseY);

		if (slot instanceof CraftingResultSlot) {
			/** - Right-click */
			if (button == 1 && slot.hasStack()) {
				/** - Abort and do normal shift key crafting if shift key is down */
				boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
				if (!isShiftKeyDown) {
					int maxStackSize = slot.getStack().getMaxCount();
					int numCrafted = 0;
					for (int craftingAttempts = 0; craftingAttempts < 256; craftingAttempts++) {
						if (slot.hasStack() && numCrafted < maxStackSize) {
							numCrafted += slot.getStack().count;
							inventoryTweaks_internalMouseClicked(mouseX, mouseY, button);
						} else {
							break;
						}
					}
					return true;
				}
			}
		}

		return false;
	}

	@Unique private boolean inventoryTweaks_handleShiftClickCrafting(int mouseX, int mouseY, int button) {
		Slot slot = this.getSlotAt(mouseX, mouseY);

		if (slot instanceof CraftingResultSlot) {
			boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
			if (true == isShiftKeyDown && slot.hasStack()) {
				for (int craftingAttempts = 0; craftingAttempts < 256; craftingAttempts++) {
					if (slot.hasStack()) {
						inventoryTweaks_internalMouseClicked(mouseX, mouseY, button);
					} else {
						break;
					}
				}
				return true;
			}
		}

		return false;
	}

	@Unique private void inventoryTweaks_internalMouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		if (button == 0 || button == 1) {
			Slot var4 = this.getSlotAt(mouseX, mouseY);
			int var5 = (this.width - this.backgroundWidth) / 2;
			int var6 = (this.height - this.backgroundHeight) / 2;
			boolean var7 = mouseX < var5 || mouseY < var6 || mouseX >= var5 + this.backgroundWidth || mouseY >= var6 + this.backgroundHeight;
			int var8 = -1;
			if (var4 != null) {
				var8 = var4.id;
			}

			if (var7) {
				var8 = -999;
			}

			if (var8 != -1) {
				boolean var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
				this.minecraft.interactionManager.clickSlot(this.container.syncId, var8, button, var9, this.minecraft.player);
			}
		}
	}

	@Inject(method = "mouseReleased", at = @At("RETURN"), cancellable = true)
	private void inventoryTweaks_mouseReleasedOrSlotChanged(int mouseX, int mouseY, int button, CallbackInfo ci) {
		slot = this.getSlotAt(mouseX, mouseY);

		/** - Do nothing if mouse is not over a slot */
		if (slot == null)
			return;

		if (Config.INVENTORY_TWEAKS_CONFIG.MOUSE_TWEAKS_CONFIG.SCROLL_WHEEL_CONFIG.enableScrollWheelTweaks) {
			if (!minecraft.world.isRemote) {
				int currentWheelDegrees = Mouse.getDWheel();
				if ((0 != currentWheelDegrees)
						&& (isLeftClickDragStarted == false)
						&& (isRightClickDragStarted == false)
				) {
					inventoryTweaks_handleScrollWheel(currentWheelDegrees);
				}
			}
		}

		/** - Right-click + Drag logic = distribute one item from held items to each slot */
		if (  ( button == -1 )
		   && ( Mouse.isButtonDown(1) )
		   && ( isLeftClickDragStarted == false )
		   && ( isLeftClickDragMouseTweaksStarted == false )
		   && ( rightClickPersistentStack != null )
		) {
			ItemStack slotItemToExamine = slot.getStack();

			/** - Do nothing if slot item does not match held item or if the slot is full */
			if (  (null != slotItemToExamine)
			   && (  (!slotItemToExamine.isItemEqual(rightClickPersistentStack))
				  || (slotItemToExamine.count == rightClickPersistentStack.getMaxCount())
			      )
			) {
				return;
			}

			/** - Do nothing if there are no more items to distribute */
			ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
			if (null == cursorStack) {
				return;
			}

			if (!rightClickHoveredSlots.contains(slot)) {
				inventoryTweaks_handleRightClickDrag(slotItemToExamine);
			} else if (Config.INVENTORY_TWEAKS_CONFIG.MOUSE_TWEAKS_CONFIG.RMBTweak) {
				inventoryTweaks_handleRightClickDragMouseTweaks();
			}
		} else {
			inventoryTweaks_resetRightClickDragVariables();
		}

		/** - Left-click + Drag logic = evenly distribute held items over slots */
		if (  ( button == -1 )
		   && ( Mouse.isButtonDown(0) )
		   && ( isRightClickDragStarted == false )
		) {
			if (isLeftClickDragMouseTweaksStarted) {
				inventoryTweaks_handleLeftClickDragMouseTweaks();
			} else if ( leftClickPersistentStack != null ) {
				if (inventoryTweaks_handleLeftClickDrag()) {
					return;
				}
			} else {
				inventoryTweaks_resetLeftClickDragVariables();
			}
		} else {
			inventoryTweaks_resetLeftClickDragVariables();
		}
	}

	@Unique private void inventoryTweaks_handleScrollWheel(int wheelDegrees) {
		ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
		ItemStack slotItemToExamine = slot.getStack();

		if (  (null != cursorStack)
		   || (null != slotItemToExamine)
		   )
		{
			//boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
			boolean transferAllowed = true;
			float numberOfTurns = (float)wheelDegrees / 120.0f;
			int cursorStackAmount = 0;
			int slotStackAmount = 0;
			ItemStack itemBeingTransfered = null;

			if (null != cursorStack) {
				itemBeingTransfered = cursorStack;
				cursorStackAmount = cursorStack.count;
			}

			if (null != slotItemToExamine) {
				itemBeingTransfered = slotItemToExamine;
				slotStackAmount = slotItemToExamine.count;
			}

			/** - Allow transfers if one or both of the slots are empty */
			if (  (null != cursorStack)
			   && (null != slotItemToExamine)
			) {
				/** - Prevent transfers if items in slots do not match */
				transferAllowed = cursorStack.isItemEqual(slotItemToExamine);
			}

			/** - Prevent illegal transfers that can cause bugs/dupes */
			if (  (slot.id == 0) && (container instanceof CraftingScreenHandler)
			   || (slot.id == 2) && (container instanceof FurnaceScreenHandler)
			   || (  (container instanceof PlayerScreenHandler)
				  && (  (slot.id == 0)
					 ||	(slot.id == 5)
					 ||	(slot.id == 6)
					 ||	(slot.id == 7)
					 ||	(slot.id == 8)
				     )
			      )
			) {
				transferAllowed = false;
			}

			if (transferAllowed) {
				inventoryTweaks_scrollCursorSlotTransfer(numberOfTurns, cursorStackAmount, slotStackAmount, itemBeingTransfered);
			}
//			if (isShiftKeyDown) {
//				if (Config.ScrollWheelConfig.shiftScrollWheelBehavior) {
//					inventoryTweaks_scrollInventoryTransfer(numberOfTurns, cursorStackAmount, slotStackAmount, itemBeingTransfered);
//				} else {
//					inventoryTweaks_scrollCursorSlotTransfer(numberOfTurns, cursorStackAmount, slotStackAmount, itemBeingTransfered);
//				}
//			} else {
//				if (Config.ScrollWheelConfig.scrollWheelBehavior) {
//					inventoryTweaks_scrollCursorSlotTransfer(numberOfTurns, cursorStackAmount, slotStackAmount, itemBeingTransfered);
//				} else {
//					inventoryTweaks_scrollInventoryTransfer(numberOfTurns, cursorStackAmount, slotStackAmount, itemBeingTransfered);
//				}
//			}
		}
	}

	@Unique private void inventoryTweaks_scrollCursorSlotTransfer(float numTurns, int cursorAmount, int slotAmount, ItemStack transferItem) {
		if (Config.INVENTORY_TWEAKS_CONFIG.MOUSE_TWEAKS_CONFIG.SCROLL_WHEEL_CONFIG.invertScrollCursorSlotDirection) {
			numTurns *= -1;
		}

		if (0 > numTurns) {
			/** - Transfer items to slot from cursor */
			if (0 != cursorAmount) {
				for (int turnIndex = 0; turnIndex < abs(numTurns); turnIndex++) {
					if (slotAmount != transferItem.getMaxCount()) {
						if (0 == (cursorAmount - 1)) {
							minecraft.player.inventory.setCursorStack(null);
						} else {
							minecraft.player.inventory.setCursorStack(new ItemStack(transferItem.itemId, (cursorAmount - 1), transferItem.getDamage()));
						}
						slot.setStack(new ItemStack(transferItem.itemId, (slotAmount + 1), transferItem.getDamage()));
					}
				}
			}
		} else {
			/** - Transfer items to cursor from slot */
			if (0 != slotAmount) {
				for (int turnIndex = 0; turnIndex < abs(numTurns); turnIndex++) {
					if (cursorAmount != transferItem.getMaxCount()) {
						if (0 == (slotAmount - 1)) {
							slot.setStack(null);
						} else {
							slot.setStack(new ItemStack(transferItem.itemId, (slotAmount - 1), transferItem.getDamage()));
						}
						minecraft.player.inventory.setCursorStack(new ItemStack(transferItem.itemId, (cursorAmount + 1), transferItem.getDamage()));
					}
				}
			}
		}
	}

//	@Unique private void inventoryTweaks_scrollInventoryTransfer(float numTurns, int cursorAmount, int slotAmount, ItemInstance transferItem) {
//		int itemsLeftToAdd = 0;
//
//		if (Config.ScrollWheelConfig.invertScrollInventoryDirection) {
//			numTurns *= -1;
//		}
//
//		if (minecraft.player.container == minecraft.player.playerContainer) {
//			System.out.println("Only one container exists");
//		}
//
//		if (0 > numTurns) {
//			/** - Transfer items out of slot */
//			if (0 != slotAmount) {
//				for (int containerIndex = 0; containerIndex < minecraft.player.container.slots.lastIndexOf(Slot) - 1; containerIndex++) {
//					Slot curSlot = (Slot)minecraft.player.playerContainer.slots.get(containerIndex);
////					ItemInstance curSlotItem = curSlot.getItem();
////					int curSlotAmount = (null != curSlotItem) ? curSlotItem.count : 0;
////
////					if (  (null == curSlotItem)
////					   || (  (curSlotItem.isDamageAndIDIdentical(transferItem))
////					      && (curSlotAmount != transferItem.getMaxStackSize())
////					      )
////					) {
////						while (  (itemsLeftToAdd < abs(numTurns))
////						      && (curSlotAmount != transferItem.getMaxStackSize())
////					    ) {
////							if (0 == (slotAmount - 1)) {
////								slot.setStack(null);
////							} else {
////								slot.setStack(new ItemInstance(transferItem.itemId, (slotAmount - 1), transferItem.getDamage()));
////							}
////
////							curSlot.setStack(new ItemInstance(transferItem.itemId, (curSlotAmount + 1), transferItem.getDamage()));
////
////							curSlotItem = curSlot.getItem();
////							curSlotAmount = (null != curSlotItem) ? curSlotItem.count : 0;
////							itemsLeftToAdd++;
////						}
////
////						if (itemsLeftToAdd == numTurns) {
////							return;
////						}
////					}
//				}
//			}
//		} else {
//			/** - Transfer items into slot */
//			boolean itemExistsInContainer = true;
//			if (itemExistsInContainer) {
////				for (int turnIndex = 0; turnIndex < abs(numTurns); turnIndex++) {
////				}
//				System.out.println("Scroll up");
//				Slot slotToModify = (Slot)minecraft.player.container.slots.get(0);
//
//				slotToModify.setStack(new ItemInstance(transferItem.itemId, 7, transferItem.getDamage()));
//			}
//		}
//	}

	@Unique private boolean inventoryTweaks_handleRightClick(int mouseX, int mouseY) {
		/** - Get held item */
		ItemStack cursorStack = minecraft.player.inventory.getCursorStack();

		/** - Handle Right-click if an item is held */
		if (null != cursorStack) {

			/** - Ensure a slot was clicked */
			Slot clickedSlot = this.getSlotAt(mouseX, mouseY);
			if (null != clickedSlot) {

				/** - Record how many items are in the slot */
				if (null != clickedSlot.getStack()) {

					/** - Let vanilla minecraft handle right click with an item onto a different item */
					if ( !cursorStack.isItemEqual(clickedSlot.getStack()) ) {
						return false;
					}

					rightClickExistingAmount.add(clickedSlot.getStack().count);
				} else {
					rightClickExistingAmount.add(0);
				}

				/** - Begin Right-click + Drag */
				if (cursorStack != null && rightClickPersistentStack == null && isRightClickDragStarted == false) {
					rightClickPersistentStack = cursorStack;
					rightClickItemAmount = rightClickPersistentStack.count;
					isRightClickDragStarted = true;
				}

				/** - Handle initial Right-click */
				lastRMBSlotId = clickedSlot.id;
				lastRMBSlot = clickedSlot;
				if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.RMBPreferShiftClick) {
					boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
					this.minecraft.interactionManager.clickSlot(this.container.syncId, clickedSlot.id, 1, isShiftKeyDown, this.minecraft.player);

					if (isShiftKeyDown) {
						inventoryTweaks_resetRightClickDragVariables();
					}
				} else {
					this.minecraft.interactionManager.clickSlot(this.container.syncId, clickedSlot.id, 1, false, this.minecraft.player);
				}

				return true;
			}
		}

		return false;
	}

	@Unique private void inventoryTweaks_handleRightClickDragMouseTweaks() {
		if (slot.id != lastRMBSlotId) {
			ItemStack cursorStack = minecraft.player.inventory.getCursorStack();

			if (null != cursorStack ) {
				/** - Distribute one item to the slot */
				lastRMBSlotId = slot.id;
				this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 1, false, this.minecraft.player);
			}
		}
	}

	@Unique private void inventoryTweaks_handleRightClickDrag(ItemStack slotItemToExamine) {
		/** - First slot is handled instantly in mouseClicked function */
		if (slot.id != lastRMBSlotId) {
			if (0 == rightClickHoveredSlots.size())
			{
				/** - Add slot to item distribution */
				rightClickHoveredSlots.add(lastRMBSlot);
			}

			/** - Add slot to item distribution */
			rightClickHoveredSlots.add(slot);

			/** - Record how many items are in the slot */
			if (null != slotItemToExamine) {
				rightClickExistingAmount.add(slotItemToExamine.count);
			}
			else
			{
				rightClickExistingAmount.add(0);
			}

			/** - Distribute one item to the slot */
			lastRMBSlotId = slot.id;
			this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 1, false, this.minecraft.player);
		}
	}

	@Unique private boolean inventoryTweaks_cancelRightClickDrag(boolean isClientOnServer)
	{
		/** - Cancel Right-click + Drag */
		if (isRightClickDragStarted) {
			if (rightClickHoveredSlots.size() > 1) {
				/** - Slots cannot return to normal on a server */
				if (!isClientOnServer) {
					/** - Return all slots to normal */
					minecraft.player.inventory.setCursorStack(new ItemStack(rightClickPersistentStack.itemId, rightClickItemAmount, rightClickPersistentStack.getDamage()));
					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < rightClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
						if (0 != rightClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
							rightClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemStack(rightClickPersistentStack.itemId, rightClickExistingAmount.get(leftClickHoveredSlotsIndex), rightClickPersistentStack.getDamage()));
						} else {
							rightClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
						}
					}
				}

				/** - Reset Right-click + Drag variables and exit function */
				inventoryTweaks_resetRightClickDragVariables();

				return true;
			}
		}

		return false;
	}

	@Unique private void inventoryTweaks_resetRightClickDragVariables()
	{
		rightClickExistingAmount.clear();
		rightClickHoveredSlots.clear();
		rightClickPersistentStack = null;
		rightClickItemAmount = 0;
		isRightClickDragStarted = false;
	}

	@Unique private boolean inventoryTweaks_handleLeftClickWithItem(ItemStack cursorStack, Slot clickedSlot, boolean isClientOnServer) {
		/** - Ensure a slot was clicked */
		if (null != clickedSlot) {

			/** - Record how many items are in the slot and how many items are needed to fill the slot */
			if (null != clickedSlot.getStack()) {

				if (null != cursorStack) {
					/** - Let vanilla minecraft handle left click with an item onto any item */
					if (isClientOnServer) {
						return false;
					}

					/** - Let vanilla minecraft handle left click with an item onto a different item */
					if ( !cursorStack.isItemEqual(clickedSlot.getStack()) ) {
						return false;
					}
				}

				leftClickAmountToFillPersistent.add(cursorStack.getMaxCount() - clickedSlot.getStack().count);
				leftClickExistingAmount.add(clickedSlot.getStack().count);
			} else {
				leftClickAmountToFillPersistent.add(cursorStack.getMaxCount());
				leftClickExistingAmount.add(0);
			}

			/** - Begin Left-click + Drag */
			if (cursorStack != null && leftClickPersistentStack == null && isLeftClickDragStarted == false) {
				leftClickPersistentStack = cursorStack;
				leftClickItemAmount = leftClickPersistentStack.count;
				isLeftClickDragStarted = true;
			}

			/** - Handle initial Left-click */
			lastLMBSlotId = clickedSlot.id;
			lastLMBSlot = clickedSlot;
			if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.LMBPreferShiftClick) {
				boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
				this.minecraft.interactionManager.clickSlot(this.container.syncId, clickedSlot.id, 0, isShiftKeyDown, this.minecraft.player);

				if (isShiftKeyDown) {
					inventoryTweaks_resetLeftClickDragVariables();
					leftClickMouseTweaksPersistentStack = cursorStack;
					isLeftClickDragMouseTweaksStarted = true;
				}
			} else {
				this.minecraft.interactionManager.clickSlot(this.container.syncId, clickedSlot.id, 0, false, this.minecraft.player);
			}

			return true;
		}

		return false;
	}

	@Unique private boolean inventoryTweaks_handleLeftClickWithoutItem(Slot clickedSlot) {
		isLeftClickDragMouseTweaksStarted = true;

		/** - Ensure a slot was clicked */
		if (clickedSlot != null) {
			boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));

			/** - Get info for MouseTweaks `Left-Click + Drag` mechanics */
			if (!isShiftKeyDown) {
				ItemStack itemInSlot = clickedSlot.getStack();
				leftClickMouseTweaksPersistentStack = itemInSlot;
			}

			/** - Handle initial Left-click */
			lastLMBSlotId = clickedSlot.id;
			lastLMBSlot = clickedSlot;
			this.minecraft.interactionManager.clickSlot(this.container.syncId, clickedSlot.id, 0, isShiftKeyDown, this.minecraft.player);

			return true;
		} else {
			/** - Get info for MouseTweaks `Left-Click + Drag` mechanics */
			leftClickMouseTweaksPersistentStack = null;
		}

		return false;
	}

	@Unique private void inventoryTweaks_handleLeftClickDragMouseTweaks() {
		if (slot.id != lastLMBSlotId) {
			lastLMBSlotId = slot.id;

			ItemStack slotItemToExamine = slot.getStack();
			if (null != slotItemToExamine)
			{
				if (null != leftClickMouseTweaksPersistentStack)
				{
					if (slotItemToExamine.isItemEqual(leftClickMouseTweaksPersistentStack))
					{
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
							if (Config.INVENTORY_TWEAKS_CONFIG.MOUSE_TWEAKS_CONFIG.LMBTweakShiftClick)
							{
								this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, true, this.minecraft.player);
							}
						} else {
							if (Config.INVENTORY_TWEAKS_CONFIG.MOUSE_TWEAKS_CONFIG.LMBTweakPickUp) {
								ItemStack cursorStack = minecraft.player.inventory.getCursorStack();

								if (cursorStack == null) {
									/** - Pick up items from slot */
									this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
								} else if (cursorStack.count < leftClickMouseTweaksPersistentStack.getMaxCount()) {
									int amountAbleToPickUp = leftClickMouseTweaksPersistentStack.getMaxCount() - cursorStack.count;
									int amountInSlot = slotItemToExamine.count;

									/** - Pick up items from slot */
									if (amountInSlot <= amountAbleToPickUp) {
										this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
										this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
									} else if (cursorStack.count == leftClickMouseTweaksPersistentStack.getMaxCount()) {
										slot.setStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, cursorStack.count, leftClickMouseTweaksPersistentStack.getDamage()));
										minecraft.player.inventory.setCursorStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, amountInSlot, leftClickMouseTweaksPersistentStack.getDamage()));
									} else {
										this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);

										slotItemToExamine = slot.getStack();
										cursorStack = minecraft.player.inventory.getCursorStack();
										amountInSlot = slotItemToExamine.count;

										slot.setStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, cursorStack.count, leftClickMouseTweaksPersistentStack.getDamage()));
										minecraft.player.inventory.setCursorStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, amountInSlot, leftClickMouseTweaksPersistentStack.getDamage()));
									}
								}
							}
						}
					}
				} else if (  (Config.INVENTORY_TWEAKS_CONFIG.MOUSE_TWEAKS_CONFIG.LMBTweakShiftClickAny)
						&& (  (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
						|| (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
				)
				) {
					this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, true, this.minecraft.player);
				}
			}
		}
	}

	@Unique private boolean inventoryTweaks_handleLeftClickDrag()
	{
		/** - Do nothing if slot has already been added to Left-click + Drag logic */
		if (!leftClickHoveredSlots.contains(slot)) {
			ItemStack slotItemToExamine = slot.getStack();

			/** - Check if client is on a server */
			boolean isClientOnServer = minecraft.world.isRemote;

			/** - Do nothing if slot item does not match held item */
			if (null != slotItemToExamine){

				if (isClientOnServer) {
					return true;
				}

				if (!slotItemToExamine.isItemEqual(leftClickPersistentStack)) {
					return true;
				}
			}

			/** - Do nothing if there are no more items to distribute */
			if (1.0 == (double)leftClickItemAmount / (double)leftClickHoveredSlots.size()) {
				return true;
			}

			/** - First slot is handled instantly in mouseClicked function */
			if (slot.id != lastLMBSlotId) {
				if (0 == leftClickHoveredSlots.size())
				{
					/** - Add slot to item distribution */
					leftClickHoveredSlots.add(lastLMBSlot);
				}

				/** - Add slot to item distribution */
				leftClickHoveredSlots.add(slot);

				/** - Record how many items are in the slot and how many items are needed to fill the slot */
				if (null != slotItemToExamine) {
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxCount() - slotItemToExamine.count);
					leftClickExistingAmount.add(slotItemToExamine.count);
				}
				else
				{
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxCount());
					leftClickExistingAmount.add(0);
				}

				/** - Slots cannot return to normal on a server */
				List<Integer> leftClickAmountToFill = new ArrayList<>();
				if (!isClientOnServer) {
					/** - Return all slots to normal */
					minecraft.player.inventory.setCursorStack(new ItemStack(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
						leftClickAmountToFill.add(leftClickAmountToFillPersistent.get(leftClickHoveredSlotsIndex));
						if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
							leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemStack(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
						} else {
							leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
						}
					}
				}

				/** - Prepare to distribute over slots */
				int numberOfSlotsRemainingToFill = leftClickHoveredSlots.size();
				int itemsPerSlot = leftClickItemAmount / numberOfSlotsRemainingToFill;
				int leftClickRemainingItemAmount = leftClickItemAmount;
				boolean rerunLoop;

				/** - Slots cannot return to normal on a server */
				if (!isClientOnServer) {
					/** - Distribute fewer items to slots whose max stack size will be filled */
					do {
						rerunLoop = false;
						if (0 < numberOfSlotsRemainingToFill) {
							itemsPerSlot = leftClickRemainingItemAmount / numberOfSlotsRemainingToFill;

							if (0 != itemsPerSlot) {
								for (int slotsToCheckIndex = 0; slotsToCheckIndex < leftClickAmountToFill.size(); slotsToCheckIndex++) {
									if (0 != leftClickAmountToFill.get(slotsToCheckIndex) && leftClickAmountToFill.get(slotsToCheckIndex) < itemsPerSlot) {
										/** - Just fill the slot and return */
										for (int fillTheAmountIndex = 0; fillTheAmountIndex < leftClickAmountToFill.get(slotsToCheckIndex); fillTheAmountIndex++) {
											this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(slotsToCheckIndex).id, 1, false, this.minecraft.player);
										}

										leftClickRemainingItemAmount = leftClickRemainingItemAmount - leftClickAmountToFill.get(slotsToCheckIndex);
										leftClickAmountToFill.set(slotsToCheckIndex, 0);
										numberOfSlotsRemainingToFill--;
										rerunLoop = true;
									}
								}
							}
						}
					} while (rerunLoop && 0 < numberOfSlotsRemainingToFill);
				} else {
					/** - Return slots to normal on when client is on a server */
					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < (leftClickHoveredSlots.size() - 1); leftClickHoveredSlotsIndex++)
					{
						ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
						if (leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).hasStack() && leftClickHoveredSlots.size() > 1)
						{
							if (cursorStack != null)
							{
								this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
							}
							this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
						}
					}
				}

				/** - Distribute remaining items evenly over remaining slots that were not already filled to max stack size */
				for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++) {
					if (isClientOnServer) {
						if (0 != leftClickAmountToFillPersistent.get(distributeSlotsIndex)) {
							for (int addSlotIndex = 0; addSlotIndex < itemsPerSlot; addSlotIndex++) {
								this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
							}
						}
					} else {
						if (0 != leftClickAmountToFill.get(distributeSlotsIndex)) {
							for (int addSlotIndex = 0; addSlotIndex < itemsPerSlot; addSlotIndex++) {
								this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Unique private boolean inventoryTweaks_cancelLeftClickDrag(boolean isClientOnServer)
	{
		/** - Cancel Left-click + Drag */
		if (isLeftClickDragStarted) {
			if (leftClickHoveredSlots.size() > 1) {
				/** - Check if client is running on a server or not */
				if (!isClientOnServer) {
					/** - Return all slots to normal */
					minecraft.player.inventory.setCursorStack(new ItemStack(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
						if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
							leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemStack(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
						} else {
							leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
						}
					}
				} else {
					/** - Return slots to normal on when client is on a server */
					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < (leftClickHoveredSlots.size() - 1); leftClickHoveredSlotsIndex++)
					{
						ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
						if (leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).hasStack() && leftClickHoveredSlots.size() > 1)
						{
							if (cursorStack != null)
							{
								this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
							}
							this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
						}
					}
					this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get((leftClickHoveredSlots.size() - 1)).id, 0, false, this.minecraft.player);
					this.minecraft.interactionManager.clickSlot(this.container.syncId, leftClickHoveredSlots.get((leftClickHoveredSlots.size() - 1)).id, 0, false, this.minecraft.player);
				}

				/** - Reset Left-click + Drag variables and exit function */
				inventoryTweaks_resetLeftClickDragVariables();
				return true;
			}
		}

		return false;
	}

	@Unique private void inventoryTweaks_resetLeftClickDragVariables()
	{
		leftClickExistingAmount.clear();
		leftClickAmountToFillPersistent.clear();
		leftClickHoveredSlots.clear();
		leftClickPersistentStack = null;
		leftClickMouseTweaksPersistentStack = null;
		leftClickItemAmount = 0;
		isLeftClickDragStarted = false;
		isLeftClickDragMouseTweaksStarted = false;
	}

	@Unique
	private boolean drawingHoveredSlot;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;II)Z"))
	private boolean inventoryTweaks_isMouseOverSlot(HandledScreen guiContainer, Slot slot, int x, int y) {
		if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.EnableDragGraphics) {
			return (  (drawingHoveredSlot = rightClickHoveredSlots.contains(slot))
				   || (drawingHoveredSlot = leftClickHoveredSlots.contains(slot))
				   || isPointOverSlot(slot, x, y)
				   );
		} else {
			return isPointOverSlot(slot, x, y);
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;fillGradient(IIIIII)V", ordinal = 0))
	private void inventoryTweaks_fillGradient(HandledScreen instance, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.EnableDragGraphics) {
			if (colorStart != colorEnd) throw new AssertionError();
			int color = drawingHoveredSlot ? 0x20ffffff : colorStart;
			this.fillGradient(startX, startY, endX, endY, color, color);
		} else {
			this.fillGradient(startX, startY, endX, endY, colorStart, colorEnd);
		}
	}

	@Inject(method = "keyPressed", at = @At("RETURN"))
	private void inventoryTweaks_keyPressed(char character, int keyCode, CallbackInfo ci) {
		if (this.slot == null) {
			return;
		}

		if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.UseDropKeyInInventory) {
			if (keyCode == this.minecraft.options.dropKey.code) {
				if (this.minecraft.player.inventory.getCursorStack() != null) {
					return;
				}

				this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
				if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.LCtrlStackDrop) {
					this.minecraft.interactionManager.clickSlot(this.container.syncId, -999, Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 0 : 1, false, this.minecraft.player);
				} else {
					this.minecraft.interactionManager.clickSlot(this.container.syncId, -999, 1, false, this.minecraft.player);
				}
				this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
			}
		}

		if (Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.NumKeyHotbarSwap) {
			if (keyCode >= Keyboard.KEY_1 && keyCode <= Keyboard.KEY_9) {
				if (  (null != this.container.slots)
				   && (10 <= this.container.slots.size())
				) {
					if (this.minecraft.player.inventory.getCursorStack() == null) {
						this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
					}
					this.minecraft.interactionManager.clickSlot(this.container.syncId, (this.container.slots.size() - 10) + keyCode - 1, 0, false, this.minecraft.player);
					this.minecraft.interactionManager.clickSlot(this.container.syncId, slot.id, 0, false, this.minecraft.player);
				}
			}
		}
	}
}
