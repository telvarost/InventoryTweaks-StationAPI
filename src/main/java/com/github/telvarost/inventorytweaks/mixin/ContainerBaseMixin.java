package com.github.telvarost.inventorytweaks.mixin;

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

	@Unique
	private int leftClickMaxStackSize;

	@Unique
	private int rightClickMaxStackSize;

	@Unique
	private int leftClickNumberOfSlotsWithItems;

	@Unique
	private int rightClickNumberOfSlotsWithItems;

	@Unique
	private int leftClickAmountToFill;

	@Unique
	private int rightClickAmountToFill;

	@Unique
	private int leftClickExistingAmount;

	@Unique
	private int rightClickExistingAmount;

	@Unique
	private int leftClickItemAmount;

	@Unique
	private int rightClickItemAmount;

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

	@Unique final List<ItemInstance> leftClickSlotItems = new ArrayList<>();

	@Unique final List<ItemInstance> rightClickSlotItems = new ArrayList<>();

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	protected void inventoryTweaks_mouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci)
	{
		if (button == 1)
		{
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			if (isLeftClickDragStarted)
			{
				if (leftClickHoveredSlots.size() > 1) {
					super.mouseClicked(mouseX, mouseY, button);

					for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++)
					{
						cursorStack = minecraft.player.inventory.getCursorItem();
						if (leftClickHoveredSlots.get(distributeSlotsIndex).hasItem())
						{
							if (cursorStack != null)
							{
								this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 0, false, this.minecraft.player);
							}

							this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 0, false, this.minecraft.player);
						}
					}

					leftClickPersistentStack = null;
					leftClickHoveredSlots.clear();
					leftClickItemAmount = 0;
					isLeftClickDragStarted = false;
					ci.cancel();
					return;
				}
			}

			if (cursorStack != null)
			{
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (clickedSlot != null)
				{
					super.mouseClicked(mouseX, mouseY, button);

					if (cursorStack != null && rightClickPersistentStack == null && isRightClickDragStarted == false)
					{
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

		if (button == 0)
		{
			ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

			if (isRightClickDragStarted)
			{
				if (rightClickHoveredSlots.size() > 1) {
					super.mouseClicked(mouseX, mouseY, button);

					for (int distributeSlotsIndex = 0; distributeSlotsIndex < rightClickHoveredSlots.size(); distributeSlotsIndex++)
					{
						cursorStack = minecraft.player.inventory.getCursorItem();
						if (rightClickHoveredSlots.get(distributeSlotsIndex).hasItem())
						{
							if (cursorStack != null)
							{
								this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, rightClickHoveredSlots.get(distributeSlotsIndex).id, 0, false, this.minecraft.player);
							}

							this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, rightClickHoveredSlots.get(distributeSlotsIndex).id, 0, false, this.minecraft.player);
						}
					}

					rightClickHoveredSlots.clear();
					rightClickItemAmount = 0;
					isRightClickDragStarted = false;
					ci.cancel();
					return;
				}
			}

			if (cursorStack != null)
			{
				Slot clickedSlot = this.getSlot(mouseX, mouseY);
				if (clickedSlot != null)
				{
					if (!clickedSlot.hasItem())
					{
						super.mouseClicked(mouseX, mouseY, button);

						if (cursorStack != null && leftClickPersistentStack == null && isLeftClickDragStarted == false) {
							leftClickPersistentStack = cursorStack;
							leftClickItemAmount = leftClickPersistentStack.count;
							isLeftClickDragStarted = true;
						}

						this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, clickedSlot.id, 0, false, this.minecraft.player);
						ci.cancel();
						return;
					}
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

		if (slot == null)
			return;

		if (button == -1 && Mouse.isButtonDown(0) && isRightClickDragStarted == false) {

			if (leftClickPersistentStack != null)
			{
				if (!leftClickHoveredSlots.contains(slot)) {
					if (slot.hasItem() && !slot.getItem().isDamageAndIDIdentical(leftClickPersistentStack)) {
						return;
					}

					if (1.0 == (double)leftClickItemAmount / (double)leftClickHoveredSlots.size())
					{
						return;
					}

					leftClickHoveredSlots.add(slot);
					leftClickSlotItems.add(slot.getItem());
					ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();

					if (1 == leftClickSlotItems.size())
					{
						leftClickNumberOfSlotsWithItems = 0;
						leftClickExistingAmount = 0;
						leftClickAmountToFill = 0;

						if (null != cursorStack)
						{
							leftClickMaxStackSize = cursorStack.getMaxStackSize();
						}
						else
						{
							leftClickMaxStackSize = 0;
						}
					}

					if (slot.hasItem())
					{
						leftClickNumberOfSlotsWithItems++;
					}

					for (int checkExistingItemCountIndex = 0; checkExistingItemCountIndex < leftClickSlotItems.size(); checkExistingItemCountIndex++)
					{
						ItemInstance currentItemToCheck =  leftClickSlotItems.get(checkExistingItemCountIndex);

						if (null != currentItemToCheck)
						{
							leftClickExistingAmount += currentItemToCheck.count;
							leftClickAmountToFill += leftClickMaxStackSize - currentItemToCheck.count;
						}
					}

					int itemsPerSlot = leftClickItemAmount / leftClickHoveredSlots.size();
					int subtractItemsFromDistribution = leftClickAmountToFill / leftClickNumberOfSlotsWithItems;
					int distributionOverEmptySlots = (leftClickItemAmount - leftClickAmountToFill) / (leftClickHoveredSlots.size() - leftClickNumberOfSlotsWithItems);

//					int itemsPerSlotWithExistingItems = (leftClickExistingAmount + leftClickItemAmount) / leftClickHoveredSlots.size();
//					if (itemsPerSlotWithExistingItems > leftClickMaxStackSize)
//					{
//					}

					for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < (leftClickHoveredSlots.size() - 1); leftClickHoveredSlotsIndex++)
					{
						cursorStack = minecraft.player.inventory.getCursorItem();
						if (leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).hasItem() && leftClickHoveredSlots.size() > 1)
						{
							if (null != cursorStack)
							{
								this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
							}

							this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
						}
					}

					if (leftClickHoveredSlots.size() > 1) {

						for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++)
						{
							if (!leftClickHoveredSlots.get(distributeSlotsIndex).hasItem())
							{
								for (int addFirstSlotIndex = 0; addFirstSlotIndex < itemsPerSlot; addFirstSlotIndex++)
								{
									this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
								}
							}
						}
					}
				}
			}
			else
			{
				leftClickHoveredSlots.clear();
				leftClickItemAmount = 0;
				isLeftClickDragStarted = false;
			}
		} else {
			leftClickPersistentStack = null;
			leftClickHoveredSlots.clear();
			leftClickItemAmount = 0;
			isLeftClickDragStarted = false;
		}

		if (button == -1 && Mouse.isButtonDown(1) && isLeftClickDragStarted == false)  {

			if (rightClickPersistentStack != null)
			{
				if (!rightClickHoveredSlots.contains(slot)) {
					if (slot.hasItem() && !slot.getItem().isDamageAndIDIdentical(rightClickPersistentStack)) {
						return;
					}

					if (1.0 == (double)rightClickItemAmount / (double)rightClickHoveredSlots.size())
					{
						return;
					}

					rightClickHoveredSlots.add(slot);

					if (rightClickHoveredSlots.size() > 1) {
						this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 1, false, this.minecraft.player);
					}
				}
			} else {
				rightClickHoveredSlots.clear();
				rightClickItemAmount = 0;
				isRightClickDragStarted = false;
			}
		} else {
			rightClickPersistentStack = null;
			rightClickHoveredSlots.clear();
			rightClickItemAmount = 0;
			isRightClickDragStarted = false;
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
