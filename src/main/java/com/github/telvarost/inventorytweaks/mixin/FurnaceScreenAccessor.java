package com.github.telvarost.inventorytweaks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(FurnaceScreen.class)
public interface FurnaceScreenAccessor {

    @Accessor(value = "furnaceBlockEntity")
    public FurnaceBlockEntity getFurnaceBlockEntity();
}
