package com.github.telvarost.inventorytweaks;

import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;

public class Config {

    @GConfig(value = "config", visibleName = "InventoryTweaks Config")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {

        @ConfigName("Add day counter to F3 overlay")
        public static Boolean ADD_DAY_COUNTER = true;

        @ConfigName("Add more sounds")
        public static Boolean ADD_MORE_SOUNDS = true;

        @ConfigName("Add \"Shift + DROP_KEY\" to drop item stack")
        public static Boolean STACK_DROP = true;

        @ConfigName("Allow sugarcane to be placed on sand")
        public static Boolean sugarCaneFixesEnabled = true;

        @ConfigName("Allow pressure plates to be placed on fences")
        public static Boolean pressurePlateFixesEnabled = true;

        @ConfigName("Disable beds")
        public static Boolean DISABLE_BEDS = false;

        @ConfigName("Disable id tags")
        public static Boolean DISABLE_ID_TAGS = true;

        @ConfigName("Disable nightmares")
        public static Boolean DISABLE_NIGHTMARES = false;

        @ConfigName("Fix bow models")
        public static Boolean FIX_BOW_MODEL = true;

//        @ConfigName("Fix double doors")
//        public static Boolean FIX_DOUBLE_DOORS = true;

        @ConfigName("Fix fishing")
        public static Boolean FIX_FISHING = true;

        @ConfigName("Fix furnace lava bucket")
        public static Boolean FIX_FURNACE_LAVA_BUCKET = true;

        @ConfigName("Fix leg armor on vehicles")
        public static Boolean FIX_LEG_ARMOR_ON_VEHICLES = true;

        @ConfigName("Fix minecart flickering")
        public static Boolean FIX_MINECART_FLICKERING = true;

        @ConfigName("Fix minecart stopping on items")
        public static Boolean FIX_MINECART_STOPPING_ON_ITEMS = true;

    }
}
