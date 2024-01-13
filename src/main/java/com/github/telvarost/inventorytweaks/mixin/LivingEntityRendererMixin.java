package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelBase;
import net.minecraft.entity.Living;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@Shadow
	protected EntityModelBase field_909;
	
	@Shadow
	protected EntityModelBase model;
	
	@Inject(method = "method_821", at = @At("HEAD"), cancellable = true)
	public void inventoryTweaks_method_821(Living entity, double d, double d1, double d2, CallbackInfo ci) {
		if (Config.ConfigFields.DISABLE_ID_TAGS) {
			ci.cancel();
		}
	}
}
