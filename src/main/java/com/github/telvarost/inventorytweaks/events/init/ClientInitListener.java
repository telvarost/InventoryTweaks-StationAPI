package com.github.telvarost.inventorytweaks.events.init;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.block.BlockBase;
import net.minecraft.client.render.block.GrassColour;
import net.modificationstation.stationapi.api.client.event.color.item.ItemColorsRegisterEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;

public class ClientInitListener {
    @EventListener(priority = ListenerPriority.HIGHEST)
    public void preInit(InitEvent event) {
        FabricLoader.getInstance().getEntrypointContainers("inventorytweaks:event_bus", Object.class).forEach(EntrypointManager::setup);
    }

    @EventListener
    public void registerColorProviders(ItemColorsRegisterEvent event) {
        event.itemColors.register(
                (item, damage) -> GrassColour.get(0.5F, 0.5F),
                BlockBase.GRASS
        );
    }
}
