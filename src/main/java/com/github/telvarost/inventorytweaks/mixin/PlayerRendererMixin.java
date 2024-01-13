package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.entity.model.Biped;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.item.ItemBase;
import net.minecraft.item.armour.Armour;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin extends LivingEntityRendererMixin {

	@Shadow
	private Biped field_295; // Armor

	@Shadow
	private Biped field_296; // Legs

	@Inject(method = "method_342", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_556;method_1862(Lnet/minecraft/entity/Living;Lnet/minecraft/item/ItemInstance;)V", shift = Shift.BEFORE))
	public void inventoryTweaks_playerRendering(PlayerBase player, float f, CallbackInfo ci) {
		if (Config.ConfigFields.FIX_BOW_MODEL) {
			ItemInstance item = player.inventory.getHeldItem();
			if (item != null && item.itemId == ItemBase.bow.id) {
				GL11.glTranslatef(0.0F, -0.5F, 0.0F);
			}
		}
	}

	@Inject(method = "method_341", at = @At("HEAD"))
	public void inventoryTweaks_render(PlayerBase arg, double d, double d1, double d2, float f, float f1, CallbackInfo ci) {
		if (Config.ConfigFields.FIX_LEG_ARMOR_ON_VEHICLES) {
			ItemInstance stack = arg.inventory.getArmourItem(1);
			if (stack != null) {
				ItemBase item = stack.getType();
				if (item instanceof Armour) {
					this.field_296.isRiding = this.field_909.isRiding;
				}
			}
		}
	}
}
