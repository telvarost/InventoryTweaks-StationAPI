package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import com.github.telvarost.inventorytweaks.ModHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.menu.VideoSettings;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.resource.language.TranslationStorage;

@Mixin(VideoSettings.class)
public class VideoSettingsScreenMixin extends ScreenMixin {

	@Inject(method = "init", at = @At("TAIL"))
	public void inventoryTweaks_init(CallbackInfo ci) {
		VideoSettings screen = (VideoSettings)(Object)this;
		this.buttons.add(new Button(300, screen.width / 2 - 155, screen.height / 6 + 96, 150, 20, this.inventoryTweaks_getCloudsLabel()));
	}

	@Inject(method = "buttonClicked", at = @At("HEAD"))
	public void inventoryTweaks_buttonClicked(Button btn, CallbackInfo ci) {
		if(btn.active) {
			if(btn.id == 300) {
				ModHelper.ModHelperFields.ENABLE_CLOUDS = !ModHelper.ModHelperFields.ENABLE_CLOUDS;
			}
		}
	}

	private String inventoryTweaks_getCloudsLabel() {
		TranslationStorage i18n = TranslationStorage.getInstance();
		return "Clouds: " + (ModHelper.ModHelperFields.ENABLE_CLOUDS ? i18n.translate("options.on") : i18n.translate("options.off"));
	}
}
