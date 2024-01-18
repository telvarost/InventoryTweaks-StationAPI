package com.github.telvarost.inventorytweaks.mixin;

import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.item.ItemBase;
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

	@Unique
	private ItemInstance leftClickPersistentStack;

	@Unique
	private ItemInstance rightClickPersistentStack;

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

	@Unique List<Integer> rightClickAmountToFillPersistent = new ArrayList<>();

	@Unique private void inventoryTweaks_resetLeftClickDragVariables()
	{
		leftClickExistingAmount.clear();
		leftClickAmountToFillPersistent.clear();
		leftClickHoveredSlots.clear();
		leftClickPersistentStack = null;
		leftClickItemAmount = 0;
		isLeftClickDragStarted = false;
	}

	@Unique private void inventoryTweaks_resetRightClickDragVariables()
	{
		rightClickExistingAmount.clear();
		rightClickHoveredSlots.clear();
		rightClickPersistentStack = null;
		rightClickItemAmount = 0;
		isRightClickDragStarted = false;
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	protected void inventoryTweaks_mouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {

		/** - Right-click */
		if (button == 1) {
			/** - Get held item */
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			/** - Cancel Left-click + Drag */
			if (isLeftClickDragStarted) {
				if (leftClickHoveredSlots.size() > 1) {
					/** - Handle if a button was clicked */
					super.mouseClicked(mouseX, mouseY, button);

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
					ci.cancel();
					return;
				}
			}

			/** - Handle Right-click if an item is held */
			if (cursorStack != null) {
				/** - Ensure a slot was clicked */
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (clickedSlot != null) {
					/** - Handle if a button was clicked */
					super.mouseClicked(mouseX, mouseY, button);

					/** - Begin Right-click + Drag */
					if (cursorStack != null && rightClickPersistentStack == null && isRightClickDragStarted == false) {
						rightClickPersistentStack = cursorStack;
						rightClickItemAmount = rightClickPersistentStack.count;
						isRightClickDragStarted = true;
					}

					/** - Handle initial Right-click */
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 1, false, this.minecraft.player);
					ci.cancel();
					return;
				}
			}
		}

		/** - Left-click */
		if (button == 0) {
			/** - Get held item */
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			/** - Cancel Right-click + Drag */
			if (isRightClickDragStarted) {
				if (rightClickHoveredSlots.size() > 1) {
					/** - Handle if a button was clicked */
					super.mouseClicked(mouseX, mouseY, button);

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
					ci.cancel();
					return;
				}
			}

			/** - Handle Left-click if an item is held */
			if (cursorStack != null) {
				/** - Ensure a slot was clicked */
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (clickedSlot != null) {
					//if (!clickedSlot.hasItem()) {
					/** - Handle if a button was clicked */
						super.mouseClicked(mouseX, mouseY, button);

					/** - Begin Left-click + Drag */
						if (cursorStack != null && leftClickPersistentStack == null && isLeftClickDragStarted == false) {
							leftClickPersistentStack = cursorStack;
							leftClickItemAmount = leftClickPersistentStack.count;
							isLeftClickDragStarted = true;
						}

					/** - Handle initial Left-click */
						//this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 0, false, this.minecraft.player);
						ci.cancel();
						return;
					//}
//					else
//					{
//						ItemInstance firstRightClickSlotItem = clickedSlot.getItem();
//
//						leftClickSlotItems.add(firstRightClickSlotItem);
//
//						firstRightClickSlotItem.getMaxStackSize();
//						firstRightClickSlotItem.count
//					}
				}
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
		   && ( rightClickPersistentStack != null )
		   )
		{
			/** - Do nothing if slot has already been added to Right-click + Drag logic */
			if (!rightClickHoveredSlots.contains(slot)) {
				ItemInstance slotToItemExamine = slot.getItem();

				/** - Do nothing if slot item does not match held item */
				if (null != slotToItemExamine && !slotToItemExamine.isDamageAndIDIdentical(rightClickPersistentStack)) {
					return;
				}

				/** - Do nothing if there are no more items to distribute */
				if (1.0 == (double)rightClickItemAmount / (double)rightClickHoveredSlots.size()) {
					return;
				}

				/** - Add slot to item distribution */
				rightClickHoveredSlots.add(slot);

				/** - Record how many items are in the slot */
				if (null != slotToItemExamine) {
					rightClickExistingAmount.add(slotToItemExamine.count);
				}
				else
				{
					rightClickExistingAmount.add(0);
				}

				/** - Distribute one item to the slot (first slot happens instantly in mouseClicked function) */
				if (rightClickHoveredSlots.size() > 1) {
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 1, false, this.minecraft.player);
				}
			}
		} else {
			inventoryTweaks_resetRightClickDragVariables();
		}

		/** - Left-click + Drag logic = evenly distribute held items over slots */
		if (  ( button == -1 )
		   && ( Mouse.isButtonDown(0) )
		   && ( isRightClickDragStarted == false )
		   && ( leftClickPersistentStack != null )
		   )
		{
			/** - Do nothing if slot has already been added to Left-click + Drag logic */
			if (!leftClickHoveredSlots.contains(slot)) {
				ItemInstance slotToItemExamine = slot.getItem();

				/** - Do nothing if slot item does not match held item */
				if (null != slotToItemExamine && !slotToItemExamine.isDamageAndIDIdentical(leftClickPersistentStack)){
					return;
				}

				/** - Do nothing if there are no more items to distribute */
				if (1.0 == (double)leftClickItemAmount / (double)leftClickHoveredSlots.size()) {
					return;
				}

				/** - Add slot to item distribution */
				leftClickHoveredSlots.add(slot);

				/** - Record how many items are in the slot and how many items are needed to fill the slot */
				if (null != slotToItemExamine) {
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxStackSize() - slotToItemExamine.count);
					leftClickExistingAmount.add(slotToItemExamine.count);
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
				if (leftClickHoveredSlots.size() > 0) {
					for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++) {
						if (0 != leftClickAmountToFill.get(distributeSlotsIndex)) {
							for (int addSlotIndex = 0; addSlotIndex < itemsPerSlot; addSlotIndex++) {
								this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
							}
						}
					}
				}
			}
		} else {
			inventoryTweaks_resetLeftClickDragVariables();
		}
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
