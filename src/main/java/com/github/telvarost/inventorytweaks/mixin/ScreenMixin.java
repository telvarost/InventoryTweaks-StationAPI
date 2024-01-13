package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;

@Mixin(ScreenBase.class)
public class ScreenMixin {
	
	@Shadow
	public Minecraft minecraft;
	
	@Shadow
	public List buttons;
}
