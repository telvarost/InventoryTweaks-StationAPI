package com.github.telvarost.inventorytweaks;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;

public class Config {

    @GConfig(value = "inventoryTweaks", visibleName = "InventoryTweaks Config", primary = true)
    public static InventoryTweaksConfig INVENTORY_TWEAKS_CONFIG = new InventoryTweaksConfig();

    public static class InventoryTweaksConfig {

        @ConfigCategory("Modern Minecraft Config")
        public static ModernMinecraftConfig MODERN_MINECRAFT_CONFIG = new ModernMinecraftConfig();

        @ConfigCategory("MouseTweaks Config")
        public static final MouseTweaksConfig MOUSE_TWEAKS_CONFIG = new MouseTweaksConfig();
    }

    public static class ModernMinecraftConfig {

        @ConfigName("Enable [Left-Click + Drag]")
        public static Boolean EnableLeftClickDrag = true;

        @ConfigName("Enable [Right-Click + Drag]")
        public static Boolean EnableRightClickDrag = true;

        @ConfigName("Prefer [Shift-Click] over [Left-Click + Drag]")
        public static Boolean LMBPreferShiftClick = true;

        @ConfigName("Prefer [Shift-Click] over [Right-Click + Drag]")
        public static Boolean RMBPreferShiftClick = true;

        @ConfigName("Use [DROP_KEY] to drop inventory items")
        @Comment("Cursor must not be holding any items")
        public static Boolean UseDropKeyInInventory = true;

        @ConfigName("Use [LCtrl + DROP_KEY] to drop entire stack")
        public static Boolean LCtrlStackDrop = true;

        @ConfigName("Use [NUMBER_KEYS] to swap items to hotbar")
        @Comment("Hover over the slot or swap cursor item")
        public static Boolean NumKeyHotbarSwap = true;
    }

    public static class MouseTweaksConfig {

        @ConfigName("Empty cursor [Shift + Left-Click + Drag]")
        @Comment("[Shift-Click] items of any type")
        public static Boolean LMBTweakShiftClickAny = true;

        @ConfigName("Item in cursor [Shift + Left-Click + Drag]")
        @Comment("[Shift-Click] items of the held type")
        public static Boolean LMBTweakShiftClick = true;

        @ConfigName("[Right-Click + Drag] over existing slots")
        public static Boolean RMBTweak = true;

        @ConfigName("[Left-Click + Drag] to pick up items")
        public static Boolean LMBTweakPickUp = true;
    }
}
