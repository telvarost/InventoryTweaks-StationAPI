package com.github.telvarost.inventorytweaks.mixin;

import com.github.telvarost.inventorytweaks.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.MultiplayerClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MultiplayerClientPlayerEntity.class, PlayerEntity.class})
public abstract class PlayerMixin {
    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void inventoryTweaks_dropSelectedItem(CallbackInfo ci) {
        if (!Config.INVENTORY_TWEAKS_CONFIG.MODERN_MINECRAFT_CONFIG.LCtrlStackDrop) {
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            Minecraft minecraft = MinecraftAccessor.getInstance();
            PlayerEntity playerBase = (PlayerEntity) (Object) this;

            minecraft.interactionManager.clickSlot(0, 36 + playerBase.inventory.selectedSlot, 0, false, minecraft.player);
            minecraft.interactionManager.clickSlot(0, -999, 0, false, minecraft.player);
            ci.cancel();
        }
    }
}
