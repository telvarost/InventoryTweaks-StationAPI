package com.github.telvarost.inventorytweaks;

import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;

public class Config {

    @GConfig(value = "config", visibleName = "InventoryTweaks Config")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {

//        @ConfigName("Fix double doors")
//        public static Boolean FIX_DOUBLE_DOORS = true;
    }
}
