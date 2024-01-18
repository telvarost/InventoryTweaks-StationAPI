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
		leftClickPersistentStack = null;
		leftClickHoveredSlots.clear();
		leftClickItemAmount = 0;
		isLeftClickDragStarted = false;
	}

	@Unique private void inventoryTweaks_resetRightClickDragVariables()
	{
		rightClickPersistentStack = null;
		rightClickHoveredSlots.clear();
		rightClickItemAmount = 0;
		isRightClickDragStarted = false;
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	protected void inventoryTweaks_mouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
		if (button == 1) {
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			if (isLeftClickDragStarted) {
				if (leftClickHoveredSlots.size() > 1) {
					super.mouseClicked(mouseX, mouseY, button);

					minecraft.player.inventory.setCursorItem(new ItemInstance(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
						if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
							leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemInstance(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
						} else {
							leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
						}
					}

					inventoryTweaks_resetLeftClickDragVariables();
					ci.cancel();
					return;
				}
			}

			if (cursorStack != null) {
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (clickedSlot != null) {
					super.mouseClicked(mouseX, mouseY, button);

					if (cursorStack != null && rightClickPersistentStack == null && isRightClickDragStarted == false) {
						rightClickPersistentStack = cursorStack;
						rightClickItemAmount = rightClickPersistentStack.count;
						isRightClickDragStarted = true;
					}

					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 1, false, this.minecraft.player);
					ci.cancel();
					return;
				}
			}
		}

		if (button == 0) {
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			if (isRightClickDragStarted) {
				if (rightClickHoveredSlots.size() > 1) {
					super.mouseClicked(mouseX, mouseY, button);

					for (int distributeSlotsIndex = 0; distributeSlotsIndex < rightClickHoveredSlots.size(); distributeSlotsIndex++) {
						cursorStack = minecraft.player.inventory.getCursorItem();
						if (rightClickHoveredSlots.get(distributeSlotsIndex).hasItem()) {
							if (cursorStack != null) {
								this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, rightClickHoveredSlots.get(distributeSlotsIndex).id, 0, false, this.minecraft.player);
							}

							this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, rightClickHoveredSlots.get(distributeSlotsIndex).id, 0, false, this.minecraft.player);
						}
					}

					inventoryTweaks_resetRightClickDragVariables();
					ci.cancel();
					return;
				}
			}

			if (cursorStack != null) {
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (clickedSlot != null) {
					//if (!clickedSlot.hasItem()) {
						super.mouseClicked(mouseX, mouseY, button);

						if (cursorStack != null && leftClickPersistentStack == null && isLeftClickDragStarted == false) {
							leftClickPersistentStack = cursorStack;
							leftClickItemAmount = leftClickPersistentStack.count;
							isLeftClickDragStarted = true;
						}

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

				if (null != slotToItemExamine && !slotToItemExamine.isDamageAndIDIdentical(leftClickPersistentStack)) {
					return;
				}

				if (1.0 == (double)leftClickItemAmount / (double)leftClickHoveredSlots.size()) {
					return;
				}

				leftClickHoveredSlots.add(slot);
				ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();
				List<Integer> leftClickAmountToFill = new ArrayList<>();

				if (null != slotToItemExamine) {
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxStackSize() - slotToItemExamine.count);
					leftClickAmountToFill.clear();
					leftClickExistingAmount.add(slotToItemExamine.count);
				}
				else
				{
					leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxStackSize());
					leftClickAmountToFill.clear();
					leftClickExistingAmount.add(0);
				}

				/** - Return slots to normal */
				minecraft.player.inventory.setCursorItem(new ItemInstance(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
				int leftClickRemainingItemAmount = leftClickItemAmount;
				for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
					leftClickAmountToFill.add(leftClickAmountToFillPersistent.get(leftClickHoveredSlotsIndex));
					if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
						leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemInstance(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
					} else {
						leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
					}
				}

				int numberOfSlotsRemainingToFill = leftClickHoveredSlots.size();
				int itemsPerSlot = leftClickRemainingItemAmount / numberOfSlotsRemainingToFill;
				boolean rerunLoop;

				do {
					rerunLoop = false;
					if (0 != numberOfSlotsRemainingToFill) {
						itemsPerSlot = leftClickRemainingItemAmount / numberOfSlotsRemainingToFill;

						System.out.println("ItemsPerSlot: " + itemsPerSlot);
						if (0 != itemsPerSlot)
						{
							for (int slotsToCheckIndex = 0; slotsToCheckIndex < leftClickAmountToFill.size(); slotsToCheckIndex++) {
								if (0 != leftClickAmountToFill.get(slotsToCheckIndex) && leftClickAmountToFill.get(slotsToCheckIndex) < itemsPerSlot) {
									System.out.println("AddThisToSlot: " + leftClickAmountToFill.get(slotsToCheckIndex));
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

				if (leftClickHoveredSlots.size() > 0) {
					for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++) {
						if (0 != leftClickAmountToFill.get(distributeSlotsIndex)) {
							System.out.println("EvenlyDistributeThis: " + itemsPerSlot);
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

		/** - Right-click + Drag logic = distribute one item from held items to each slot */
		if (  ( button == -1 )
		   && ( Mouse.isButtonDown(1) )
		   && ( isLeftClickDragStarted == false )
		   && ( rightClickPersistentStack != null )
		   )
		{
			/** - Do nothing if slot has already been added to Right-click + Drag logic */
			if (!rightClickHoveredSlots.contains(slot)) {
				if (slot.hasItem() && !slot.getItem().isDamageAndIDIdentical(rightClickPersistentStack)) {
					return;
				}

				if (1.0 == (double)rightClickItemAmount / (double)rightClickHoveredSlots.size()) {
					return;
				}

				rightClickHoveredSlots.add(slot);

				if (rightClickHoveredSlots.size() > 1) {
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 1, false, this.minecraft.player);
				}
			}
		} else {
			inventoryTweaks_resetRightClickDragVariables();
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
