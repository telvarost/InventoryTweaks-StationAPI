package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.container.slot.Slot;
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

@Mixin(ContainerBase.class)
public abstract class ContainerBaseMixin extends ScreenBase {
	@Shadow
	protected abstract Slot getSlot(int x, int y);

	@Shadow
	public net.minecraft.container.ContainerBase container;

	@Shadow
	protected abstract boolean isMouseOverSlot(Slot slot, int x, int Y);

	@Shadow protected int containerWidth;

	@Shadow protected int containerHeight;

	@Unique
	private Slot slot;

	@Unique Slot lastRMBSlot = null;

	@Unique Slot lastLMBSlot = null;

	@Unique int lastRMBSlotId = -1;

	@Unique int lastLMBSlotId = -1;

	@Unique
	private ItemInstance leftClickMouseTweaksPersistentStack = null;

	@Unique
	private ItemInstance leftClickPersistentStack = null;

	@Unique
	private ItemInstance rightClickPersistentStack = null;

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

		/** - Right-click */
		if (button == 1) {
			boolean exitFunction = false;

			/** - Should click cancel Left-click + Drag */
			if (!inventoryTweaks_cancelLeftClickDrag()) {

				/** - Handle Right-click */
				exitFunction = inventoryTweaks_handleRightClick(mouseX, mouseY);
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
			if (!inventoryTweaks_cancelRightClickDrag()) {

				/** - Handle Left-click */
				ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (cursorStack != null) {
					exitFunction = inventoryTweaks_handleLeftClickWithItem(cursorStack, clickedSlot);
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

	@Inject(method = "mouseReleased", at = @At("RETURN"), cancellable = true)
	private void inventoryTweaks_mouseReleasedOrSlotChanged(int mouseX, int mouseY, int button, CallbackInfo ci) {
		slot = this.getSlot(mouseX, mouseY);

		/** - Do nothing if mouse is not over a slot */
		if (slot == null)
			return;

		/** - Right-click + Drag logic = distribute one item from held items to each slot */
		if (  ( button == -1 )
				&& ( Mouse.isButtonDown(1) )
				&& ( isLeftClickDragStarted == false )
				&& ( isLeftClickDragMouseTweaksStarted == false )
				&& ( rightClickPersistentStack != null )
		)
		{
			ItemInstance slotItemToExamine = slot.getItem();

			/** - Do nothing if slot item does not match held item or if the slot is full */
			if (  (null != slotItemToExamine)
					&& (  (!slotItemToExamine.isDamageAndIDIdentical(rightClickPersistentStack))
					|| (slotItemToExamine.count == rightClickPersistentStack.getMaxStackSize())
			)
			) {
				return;
			}

			/** - Do nothing if there are no more items to distribute */
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();
			if (null == cursorStack) {
				return;
			}

			if (!rightClickHoveredSlots.contains(slot)) {
				inventoryTweaks_handleRightClickDrag(slotItemToExamine);
			} else if (Config.ConfigFields.RMBTweak) {
				inventoryTweaks_handleRightClickDragMouseTweaks();
			}
		} else {
			inventoryTweaks_resetRightClickDragVariables();
		}

		/** - Left-click + Drag logic = evenly distribute held items over slots */
		if (  ( button == -1 )
				&& ( Mouse.isButtonDown(0) )
				&& ( isRightClickDragStarted == false )
		)
		{
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

	@Unique private boolean inventoryTweaks_handleRightClick(int mouseX, int mouseY) {
		/** - Get held item */
		ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

		/** - Handle Right-click if an item is held */
		if (cursorStack != null) {

			/** - Ensure a slot was clicked */
			Slot clickedSlot = this.getSlot(mouseX, mouseY);
			if (clickedSlot != null) {

				/** - Record how many items are in the slot */
				if (null != clickedSlot.getItem()) {
					rightClickExistingAmount.add(clickedSlot.getItem().count);
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
				if (Config.ConfigFields.RMBPreferShiftClick) {
					boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 1, isShiftKeyDown, this.minecraft.player);

					if (isShiftKeyDown) {
						inventoryTweaks_resetRightClickDragVariables();
					}
				} else {
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 1, false, this.minecraft.player);
				}

				return true;
			}
		}

		return false;
	}

	@Unique private void inventoryTweaks_handleRightClickDragMouseTweaks() {
		if (slot.id != lastRMBSlotId) {
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			if (null != cursorStack ) {
				/** - Distribute one item to the slot */
				lastRMBSlotId = slot.id;
				this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 1, false, this.minecraft.player);
			}
		}
	}

	@Unique private void inventoryTweaks_handleRightClickDrag(ItemInstance slotItemToExamine) {
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
			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 1, false, this.minecraft.player);
		}
	}

	@Unique private boolean inventoryTweaks_cancelRightClickDrag()
	{
		/** - Cancel Right-click + Drag */
		if (isRightClickDragStarted) {
			if (rightClickHoveredSlots.size() > 1) {

				/** - Return all slots to normal */
				minecraft.player.inventory.setCursorItem(new ItemInstance(rightClickPersistentStack.itemId, rightClickItemAmount, rightClickPersistentStack.getDamage()));
				for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < rightClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
					if (0 != rightClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
						rightClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemInstance(rightClickPersistentStack.itemId, rightClickExistingAmount.get(leftClickHoveredSlotsIndex), rightClickPersistentStack.getDamage()));
					} else {
						rightClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
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

	@Unique private boolean inventoryTweaks_handleLeftClickWithItem(ItemInstance cursorStack, Slot clickedSlot) {
		/** - Ensure a slot was clicked */
		if (clickedSlot != null) {
			/** - Record how many items are in the slot and how many items are needed to fill the slot */
			if (null != clickedSlot.getItem()) {
				leftClickAmountToFillPersistent.add(cursorStack.getMaxStackSize() - clickedSlot.getItem().count);
				leftClickExistingAmount.add(clickedSlot.getItem().count);
			} else {
				leftClickAmountToFillPersistent.add(cursorStack.getMaxStackSize());
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
			if (Config.ConfigFields.LMBPreferShiftClick) {
				boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
				this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 0, isShiftKeyDown, this.minecraft.player);

				if (isShiftKeyDown) {
					inventoryTweaks_resetLeftClickDragVariables();
					leftClickMouseTweaksPersistentStack = cursorStack;
					isLeftClickDragMouseTweaksStarted = true;
				}
			} else {
				this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 0, false, this.minecraft.player);
			}

			return true;
		}

		return false;
	}

	@Unique private boolean inventoryTweaks_handleLeftClickWithoutItem(Slot clickedSlot) {
		isLeftClickDragMouseTweaksStarted = true;

		/** - Ensure a slot was clicked */
		if (clickedSlot != null) {
			/** - Get info for MouseTweaks `Left-Click + Drag` mechanics */
			ItemInstance itemInSlot = clickedSlot.getItem();
			leftClickMouseTweaksPersistentStack = itemInSlot;

			/** - Handle initial Left-click */
			lastLMBSlotId = clickedSlot.id;
			lastLMBSlot = clickedSlot;
			boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 0, isShiftKeyDown, this.minecraft.player);

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

			ItemInstance slotItemToExamine = slot.getItem();
			if (null != slotItemToExamine)
			{
				if (null != leftClickMouseTweaksPersistentStack)
				{
					if (slotItemToExamine.isDamageAndIDIdentical(leftClickMouseTweaksPersistentStack))
					{
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
							if (Config.ConfigFields.LMBTweakWithItem)
							{
								this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, true, this.minecraft.player);
							}
						} else {
							if (Config.ConfigFields.LMBTweakWithItem) {
								ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

								if (cursorStack == null) {
									/** - Pick up items from slot */
									this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
								} else if (cursorStack.count < leftClickMouseTweaksPersistentStack.getMaxStackSize()) {
									int amountAbleToPickUp = leftClickMouseTweaksPersistentStack.getMaxStackSize() - cursorStack.count;
									int amountInSlot = slotItemToExamine.count;

									/** - Pick up items from slot */
									if (amountInSlot <= amountAbleToPickUp) {
										this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
										this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
									} else if (cursorStack.count == leftClickMouseTweaksPersistentStack.getMaxStackSize()) {
										slot.setStack(new ItemInstance(leftClickMouseTweaksPersistentStack.itemId, cursorStack.count, leftClickMouseTweaksPersistentStack.getDamage()));
										minecraft.player.inventory.setCursorItem(new ItemInstance(leftClickMouseTweaksPersistentStack.itemId, amountInSlot, leftClickMouseTweaksPersistentStack.getDamage()));
									} else {
										this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);

										slotItemToExamine = slot.getItem();
										cursorStack = minecraft.player.inventory.getCursorItem();
										amountInSlot = slotItemToExamine.count;

										slot.setStack(new ItemInstance(leftClickMouseTweaksPersistentStack.itemId, cursorStack.count, leftClickMouseTweaksPersistentStack.getDamage()));
										minecraft.player.inventory.setCursorItem(new ItemInstance(leftClickMouseTweaksPersistentStack.itemId, amountInSlot, leftClickMouseTweaksPersistentStack.getDamage()));
									}
								}
							}
						}
					}
				} else if (  (Config.ConfigFields.LMBTweakWithoutItem)
						&& (  (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
						|| (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
				)
				) {
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, true, this.minecraft.player);
				}
			}
		}
	}

	@Unique private boolean inventoryTweaks_handleLeftClickDrag()
	{
		/** - Do nothing if slot has already been added to Left-click + Drag logic */
		if (!leftClickHoveredSlots.contains(slot)) {
			ItemInstance slotItemToExamine = slot.getItem();

			/** - Do nothing if slot item does not match held item */
			if (null != slotItemToExamine && !slotItemToExamine.isDamageAndIDIdentical(leftClickPersistentStack)){
				return true;
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
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxStackSize() - slotItemToExamine.count);
					leftClickExistingAmount.add(slotItemToExamine.count);
				}
				else
				{
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxStackSize());
					leftClickExistingAmount.add(0);
				}

				/** - Return all slots to normal */
				List<Integer> leftClickAmountToFill = new ArrayList<>();
				minecraft.player.inventory.setCursorItem(new ItemInstance(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
				for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
					leftClickAmountToFill.add(leftClickAmountToFillPersistent.get(leftClickHoveredSlotsIndex));
					if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
						leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemInstance(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
					} else {
						leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
					}
				}

				/** - Prepare to distribute over slots */
				int numberOfSlotsRemainingToFill = leftClickHoveredSlots.size();
				int itemsPerSlot = leftClickItemAmount / numberOfSlotsRemainingToFill;
				int leftClickRemainingItemAmount = leftClickItemAmount;
				boolean rerunLoop;

				/** - Distribute fewer items to slots whose max stack size will be filled */
				do {
					rerunLoop = false;
					if (0 != numberOfSlotsRemainingToFill) {
						itemsPerSlot = leftClickRemainingItemAmount / numberOfSlotsRemainingToFill;

						if (0 != itemsPerSlot)
						{
							for (int slotsToCheckIndex = 0; slotsToCheckIndex < leftClickAmountToFill.size(); slotsToCheckIndex++) {
								if (0 != leftClickAmountToFill.get(slotsToCheckIndex) && leftClickAmountToFill.get(slotsToCheckIndex) < itemsPerSlot) {
									/** - Just fill the slot and return */
									for (int fillTheAmountIndex = 0; fillTheAmountIndex < leftClickAmountToFill.get(slotsToCheckIndex); fillTheAmountIndex++) {
										this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(slotsToCheckIndex).id, 1, false, this.minecraft.player);
									}

									leftClickRemainingItemAmount = leftClickRemainingItemAmount - leftClickAmountToFill.get(slotsToCheckIndex);
									leftClickAmountToFill.set(slotsToCheckIndex, 0);
									numberOfSlotsRemainingToFill--;
									rerunLoop = true;
								}
							}
						}
					}
				} while (rerunLoop && 0 != numberOfSlotsRemainingToFill);

				/** - Distribute remaining items evenly over remaining slots that were not already filled to max stack size */
				for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++) {
					if (0 != leftClickAmountToFill.get(distributeSlotsIndex)) {
						for (int addSlotIndex = 0; addSlotIndex < itemsPerSlot; addSlotIndex++) {
							this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
						}
					}
				}
			}
		}

		return false;
	}

	@Unique private boolean inventoryTweaks_cancelLeftClickDrag()
	{
		/** - Cancel Left-click + Drag */
		if (isLeftClickDragStarted) {
			if (leftClickHoveredSlots.size() > 1) {

				/** - Return all slots to normal */
				minecraft.player.inventory.setCursorItem(new ItemInstance(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
				for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
					if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
						leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemInstance(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
					} else {
						leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
					}
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

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/container/ContainerBase;isMouseOverSlot(Lnet/minecraft/container/slot/Slot;II)Z"))
	private boolean inventoryTweaks_isMouseOverSlot(ContainerBase guiContainer, Slot slot, int x, int y) {
		return (  (drawingHoveredSlot = rightClickHoveredSlots.contains(slot))
				|| (drawingHoveredSlot = leftClickHoveredSlots.contains(slot))
				|| isMouseOverSlot(slot, x, y)
		);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/container/ContainerBase;fillGradient(IIIIII)V", ordinal = 0))
	private void inventoryTweaks_fillGradient(ContainerBase instance, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		if (colorStart != colorEnd) throw new AssertionError();
		int color = drawingHoveredSlot ? 0x20ffffff : colorStart;
		this.fillGradient(startX, startY, endX, endY, color, color);
	}

	@Inject(method = "keyPressed", at = @At("RETURN"))
	private void inventoryTweaks_keyPressed(char character, int keyCode, CallbackInfo ci) {
		if (this.slot == null)
			return;

		if (keyCode == this.minecraft.options.dropKey.key) {
			if (this.minecraft.player.inventory.getCursorItem() != null)
				return;

			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, -999, Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 0 : 1, false, this.minecraft.player);
			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
		}

		if (keyCode >= Keyboard.KEY_1 && keyCode <= Keyboard.KEY_9) {
			if (this.minecraft.player.inventory.getCursorItem() == null)
				this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, 35 + keyCode - 1, 0, false, this.minecraft.player);
			this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 0, false, this.minecraft.player);
		}
	}
}
