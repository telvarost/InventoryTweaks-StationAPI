package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import net.minecraft.client.gui.screen.ScreenBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.render.RenderHelper;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(ContainerBase.class)
public class ContainerScreenMixin extends ScreenBase {

	@Shadow
	protected int containerWidth;
	
	@Shadow
	protected int containerHeight;
	
	private static ItemRenderer itemRenderer = new ItemRenderer();

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/container/ContainerBase;renderForeground()V", shift = Shift.AFTER))
	public void inventoryTweaks_onRender(int i, int j, float f, CallbackInfo ci) {
		ContainerBase screen = ((ContainerBase) (Object) this);
		PlayerInventory selectedItem = this.minecraft.player.inventory;
		int posX = (screen.width - this.containerWidth) / 2;
		int posY = (screen.height - this.containerHeight) / 2;

		GL11.glPushMatrix();
		GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableLighting();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826);
		GL11.glEnable(2896);
		GL11.glEnable(2929);

		if (selectedItem.getCursorItem() != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			itemRenderer.method_1487(this.minecraft.textRenderer, this.minecraft.textureManager, selectedItem.getCursorItem(), i - posX - 8, j - posY - 8);
			itemRenderer.method_1488(this.minecraft.textRenderer, this.minecraft.textureManager, selectedItem.getCursorItem(), i - posX - 8, j - posY - 8);
		}

		GL11.glDisable(32826);
		RenderHelper.disableLighting();
		GL11.glDisable(2896);
		GL11.glDisable(2929);
	}
}
