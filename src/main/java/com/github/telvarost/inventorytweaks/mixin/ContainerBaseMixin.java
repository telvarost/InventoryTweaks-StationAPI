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

import java.util.HashSet;
import java.util.Set;

@Mixin(ContainerBase.class)
public abstract class ContainerBaseMixin extends ScreenBase {
	@Shadow
	protected abstract Slot getSlot(int x, int y);

	@Shadow
	public net.minecraft.container.ContainerBase container;

	@Shadow
	protected abstract boolean isMouseOverSlot(Slot slot, int x, int Y);

	@Unique
	private Slot slot;

	@Unique
	private final Set<Slot> hoveredSlots = new HashSet<>();

	@Inject(method = "mouseReleased", at = @At("RETURN"))
	private void onMouseReleased(int mouseX, int mouseY, int button, CallbackInfo ci) {
		slot = this.getSlot(mouseX, mouseY);

		if (slot == null)
			return;

		ItemInstance cursorStack = minecraft.player.inventory.getCursorItem();
		if (button == -1 && Mouse.isButtonDown(1) && cursorStack != null) {
			if (!hoveredSlots.contains(slot)) {
				if (slot.hasItem() && !slot.getItem().isDamageAndIDIdentical(cursorStack)) {
					return;
				}

				hoveredSlots.add(slot);
				if (hoveredSlots.size() > 1) {
					this.minecraft.interactionManager.clickSlot(this.container.currentContainerId, slot.id, 1, false, this.minecraft.player);
				}
			}
		} else {
			hoveredSlots.clear();
		}
	}

	@Unique
	private boolean drawingHoveredSlot;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/container/ContainerBase;isMouseOverSlot(Lnet/minecraft/container/slot/Slot;II)Z"))
	private boolean redirectIsPointOverSlot(ContainerBase guiContainer, Slot slot, int x, int y) {
		return (drawingHoveredSlot = hoveredSlots.contains(slot)) || isMouseOverSlot(slot, x, y);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/container/ContainerBase;fillGradient(IIIIII)V", ordinal = 0))
	private void redirectFillGradient(ContainerBase instance, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		if (colorStart != colorEnd) throw new AssertionError();
		int color = drawingHoveredSlot ? 0x20ffffff : colorStart;
		this.fillGradient(startX, startY, endX, endY, color, color);
	}

	@Inject(method = "keyPressed", at = @At("RETURN"))
	private void onKeyPressed(char character, int keyCode, CallbackInfo ci) {
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
