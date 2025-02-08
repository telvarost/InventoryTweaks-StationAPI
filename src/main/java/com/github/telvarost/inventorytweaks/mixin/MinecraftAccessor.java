package com.github.telvarost.inventorytweaks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor(value = "INSTANCE")
    static Minecraft getInstance() {
        throw new UnsupportedOperationException();
    }
}
