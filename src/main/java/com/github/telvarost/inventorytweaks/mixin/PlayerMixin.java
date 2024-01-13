package com.github.telvarost.inventorytweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayer;
import net.minecraft.entity.player.PlayerBase;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayer.class, PlayerBase.class})
public abstract class PlayerMixin {
    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(CallbackInfo ci) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            Minecraft minecraft = MinecraftAccessor.getInstance();
            PlayerBase playerBase = (PlayerBase) (Object) this;

            minecraft.interactionManager.clickSlot(0, 36 + playerBase.inventory.selectedHotbarSlot, 0, false, minecraft.player);
            minecraft.interactionManager.clickSlot(0, -999, 0, false, minecraft.player);
            ci.cancel();
        }
    }
}
