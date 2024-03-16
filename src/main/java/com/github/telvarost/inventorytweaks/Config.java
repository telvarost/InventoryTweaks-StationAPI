package com.github.telvarost.inventorytweaks;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;

public class Config {

    @GConfig(value = "inventoryTweaks", visibleName = "InventoryTweaks", primary = true)
    public static InventoryTweaksConfig INVENTORY_TWEAKS_CONFIG = new InventoryTweaksConfig();

    public static class InventoryTweaksConfig {

        @ConfigCategory("Modern Minecraft Config")
        public static final ModernMinecraftConfig MODERN_MINECRAFT_CONFIG = new ModernMinecraftConfig();

        @ConfigCategory("MouseTweaks Config")
        public static final MouseTweaksConfig MOUSE_TWEAKS_CONFIG = new MouseTweaksConfig();
    }

    public static class ModernMinecraftConfig {

        @ConfigName("Enable [Click + Drag] graphics")
        public Boolean EnableDragGraphics = true;

        @ConfigName("Enable [Left-Click + Drag]")
        public Boolean EnableLeftClickDrag = true;

        @ConfigName("Enable [Right-Click + Drag]")
        public Boolean EnableRightClickDrag = true;

        @ConfigName("Prefer [Shift-Click] over [Left-Click + Drag]")
        public Boolean LMBPreferShiftClick = true;

        @ConfigName("Prefer [Shift-Click] over [Right-Click + Drag]")
        public Boolean RMBPreferShiftClick = true;

        @ConfigName("Use [DROP_KEY] to drop inventory items")
        @Comment("Cursor must not be holding any items")
        public Boolean UseDropKeyInInventory = true;

        @ConfigName("Use [LCtrl + DROP_KEY] to drop entire stack")
        public Boolean LCtrlStackDrop = true;

        @ConfigName("Use [NUMBER_KEYS] to swap items to hotbar")
        @Comment("Hover over the slot or swap cursor item")
        public Boolean NumKeyHotbarSwap = true;
    }

    public static class MouseTweaksConfig {

        @ConfigCategory("Scroll Wheel Config")
        public final ScrollWheelConfig SCROLL_WHEEL_CONFIG = new ScrollWheelConfig();

        @ConfigName("Empty cursor [Shift + Left-Click + Drag]")
        @Comment("[Shift-Click] items of any type")
        public Boolean LMBTweakShiftClickAny = true;

        @ConfigName("Item in cursor [Shift + Left-Click + Drag]")
        @Comment("[Shift-Click] items of the held type")
        public Boolean LMBTweakShiftClick = true;

        @ConfigName("[Right-Click + Drag] over existing slots")
        public Boolean RMBTweak = true;

        @ConfigName("[Left-Click + Drag] to pick up items")
        public Boolean LMBTweakPickUp = true;
    }

    public static class ScrollWheelConfig {

        @ConfigName("Enable Scroll Wheel Tweaks")
        public Boolean enableScrollWheelTweaks = true;

        @ConfigName("Invert scroll direction: cursor/slot")
        @Comment("For cursor/slot item transfer")
        public Boolean invertScrollCursorSlotDirection = false;

//        @ConfigName("Invert scroll direction: inventories")
//        @Comment("For item transfer between inventories")
//        public static Boolean invertScrollInventoryDirection = false;
//
//        @ConfigName("Position aware scrolling inventory transfer")
//        @Comment("Slot position will determine scroll direction")
//        public static Boolean positionAwareScrolling = false;
//
//        @ConfigName("Wheel slot search order (see comment)")
//        @Comment("true = first to last, false = last to first")
//        public static Boolean wheelSearchOrder = true;
//
//        @ConfigName("[ScrollWheel] transfer (see comment)")
//        @Comment("true = cursor/slot, false = inventories")
//        public static Boolean scrollWheelBehavior = true;
//
//        @ConfigName("[Shift + ScrollWheel] transfer (see comment)")
//        @Comment("true = inventories, false = cursor/slot")
//        public static Boolean shiftScrollWheelBehavior = true;
    }
}
