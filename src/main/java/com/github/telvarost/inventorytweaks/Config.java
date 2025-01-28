package com.github.telvarost.inventorytweaks;

import net.glasslauncher.mods.gcapi3.api.*;

public class Config {

    @ConfigRoot(value = "inventoryTweaks", visibleName = "InventoryTweaks")
    public static InventoryTweaksConfig INVENTORY_TWEAKS_CONFIG = new InventoryTweaksConfig();

    public static class InventoryTweaksConfig {

        @ConfigCategory(name = "Modern Minecraft Config")
        public static final ModernMinecraftConfig MODERN_MINECRAFT_CONFIG = new ModernMinecraftConfig();

        @ConfigCategory(name = "MouseTweaks Config")
        public static final MouseTweaksConfig MOUSE_TWEAKS_CONFIG = new MouseTweaksConfig();
    }

    public static class ModernMinecraftConfig {

        @ConfigEntry(name = "Enable [Click + Drag] graphics")
        public Boolean EnableDragGraphics = true;

        @ConfigEntry(name = "Enable [Left-Click + Drag]")
        public Boolean EnableLeftClickDrag = true;

        @ConfigEntry(name = "Enable [Right-Click + Drag]")
        public Boolean EnableRightClickDrag = true;

        @ConfigEntry(name = "Enable [Shift-Click] crafting")
        public Boolean EnableShiftClickCrafting = true;

        @ConfigEntry(name = "Prefer [Shift-Click] over [Left-Click + Drag]")
        public Boolean LMBPreferShiftClick = true;

        @ConfigEntry(name = "Prefer [Shift-Click] over [Right-Click + Drag]")
        public Boolean RMBPreferShiftClick = true;

        @ConfigEntry(
                name = "Use [DROP_KEY] to drop inventory items",
                description = "Cursor must not be holding any items"
        )
        public Boolean UseDropKeyInInventory = true;

        @ConfigEntry(name = "Use [LCtrl + DROP_KEY] to drop entire stack")
        public Boolean LCtrlStackDrop = true;

        @ConfigEntry(
                name = "Use [NUMBER_KEYS] to swap items to hotbar",
                description = "Hover over the slot or swap cursor item"
        )
        public Boolean NumKeyHotbarSwap = true;
    }

    public static class MouseTweaksConfig {

        @ConfigCategory(
                name = "Scroll Wheel Config",
                description = "Only works in single-player"
        )
        public final ScrollWheelConfig SCROLL_WHEEL_CONFIG = new ScrollWheelConfig();

        @ConfigEntry(
                name = "Empty cursor [Shift + Left-Click + Drag]",
                description = "[Shift-Click] items of any type"
        )
        public Boolean LMBTweakShiftClickAny = true;

        @ConfigEntry(
                name = "Item in cursor [Shift + Left-Click + Drag]",
                description = "[Shift-Click] items of the held type"
        )
        public Boolean LMBTweakShiftClick = true;

        @ConfigEntry(name = "[Right-Click + Drag] over existing slots")
        public Boolean RMBTweak = false;

        @ConfigEntry(name = "[Left-Click + Drag] to pick up items")
        public Boolean LMBTweakPickUp = true;
    }

    public static class ScrollWheelConfig {

        @ConfigEntry(
                name = "Enable Scroll Wheel Tweaks",
                description = "Does not work in multiplayer"
        )
        public Boolean enableScrollWheelTweaks = true;

        @ConfigEntry(
                name = "Invert scroll direction: cursor/slot",
                description = "For cursor/slot item transfer"
        )
        public Boolean invertScrollCursorSlotDirection = false;

//        @ConfigEntry(name = "Invert scroll direction: inventories")
//        @Comment("For item transfer between inventories")
//        public static Boolean invertScrollInventoryDirection = false;
//
//        @ConfigEntry(name = "Position aware scrolling inventory transfer")
//        @Comment("Slot position will determine scroll direction")
//        public static Boolean positionAwareScrolling = false;
//
//        @ConfigEntry(name = "Wheel slot search order (see comment)")
//        @Comment("true = first to last, false = last to first")
//        public static Boolean wheelSearchOrder = true;
//
//        @ConfigEntry(name = "[ScrollWheel] transfer (see comment)")
//        @Comment("true = cursor/slot, false = inventories")
//        public static Boolean scrollWheelBehavior = true;
//
//        @ConfigEntry(name = "[Shift + ScrollWheel] transfer (see comment)")
//        @Comment("true = inventories, false = cursor/slot")
//        public static Boolean shiftScrollWheelBehavior = true;
    }
}
