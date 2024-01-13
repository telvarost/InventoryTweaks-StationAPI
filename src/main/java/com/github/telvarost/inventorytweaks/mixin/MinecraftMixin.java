package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {

//	@Redirect(method = "init()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create()V"))
//	public void inventoryTweaks_createDisplay() throws LWJGLException {
//		// Why the fuck is this even a thing ? What was its intended purpose ? I NEED TO KNOW
//		Minecraft.field_2800 = null;
//		Display.create(new PixelFormat(0, 24, 0));
//	}

	@Inject(method = "loadSoundFromDir", at = @At("HEAD"))
	public void inventoryTweaks_loadSoundFromDir(String string, File file, CallbackInfo ci) {
		if(Config.ConfigFields.ADD_MORE_SOUNDS) {
			Minecraft mc = (Minecraft) (Object) this;
			int split = string.indexOf("/");
			String type = string.substring(0, split);
			String newSound = string.substring(split + 1);
			// For now only allow the minecart sounds, allowing all of them causes weird effects with same name sounds when the game decides which one to use
			// XXX Could always incorporate the sound3 part into the sound's name and have it accessible as sound3.random.bow for example, which would avoid the overlap with current sounds
			if (type.equalsIgnoreCase("sound3") && newSound.startsWith("minecart/")) {
				mc.soundHelper.addSound(newSound, file);
			}			
		}
	}
}
