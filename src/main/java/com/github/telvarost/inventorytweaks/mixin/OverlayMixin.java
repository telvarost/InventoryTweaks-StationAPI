package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import com.github.telvarost.inventorytweaks.ModHelper;
import net.minecraft.stat.Stats;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.InGame;
import net.minecraft.client.render.TextRenderer;

import java.time.Duration;

@Mixin(InGame.class)
public class OverlayMixin extends DrawableHelper {

	@Shadow
	private Minecraft minecraft;

	@Redirect(
		method = "renderHud",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/TextRenderer;drawTextWithShadow(Ljava/lang/String;III)V"
		)
	)
	public void inventoryTweaks_render(TextRenderer instance, String string, int i, int j, int k)  {
		if (Config.ConfigFields.ADD_DAY_COUNTER)
		{
			instance.drawTextWithShadow(string, i, j, k);
			long realDaysPlayed = Duration.ofSeconds(minecraft.statFileWriter.write(Stats.playOneMinute) / 20).toDays();
			long gameDaysPlayed = Duration.ofSeconds(minecraft.statFileWriter.write(Stats.playOneMinute) / 20).toMinutes() / 20;
			instance.drawTextWithShadow("Days Played: " + gameDaysPlayed + " (" + realDaysPlayed + ")", 2, 96, 14737632);
		}
		else
		{
			instance.drawTextWithShadow(string, i, j, k);
		}
	}
}
