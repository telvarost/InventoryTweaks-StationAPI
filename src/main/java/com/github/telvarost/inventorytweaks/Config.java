package com.github.telvarost.inventorytweaks;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;

public class Config {

    @GConfig(value = "config", visibleName = "InventoryTweaks Config")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {

        @ConfigName("Empty-hand [Shift + Left-Click + Drag]")
        @Comment("[Shift-Click] items of any type")
        public static Boolean LMBTweakWithoutItem = true;

        @ConfigName("Prefer [Shift-Click] over [Left-Click + Drag]")
        public static Boolean LMBPreferShiftClick = true;

        @ConfigName("Prefer [Shift-Click] over [Right-Click + Drag]")
        public static Boolean RMBPreferShiftClick = true;

        @ConfigName("[Right-Click + Drag] over existing slots")
        public static Boolean RMBTweak = true;

        @ConfigName("[Left-Click + Drag] to pick up items")
        @Comment("Hold [Shift] to [Shift-Click] items instead")
        public static Boolean LMBTweakWithItem = true;
    }
}
