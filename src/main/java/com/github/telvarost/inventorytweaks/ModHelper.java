package com.github.telvarost.inventorytweaks;

import java.util.HashMap;

public class ModHelper {

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static float clamp(float val, float min, float max) {
        return val < min ? min : Math.min(val, max);
    }

    public static class ModHelperFields {
        public static Boolean ENABLE_CLOUDS = true;
        public static Boolean IS_LAVA_BUCKET_CONSUMED = false;

        /** - Double door fix variables, I couldn't get it working */
//        public static final String SPAWN_TIME_TAG = "SpawnTime";
//        public static final String PLAY_TIME_TAG = "PlayTime";
//        public static long playTime;
//
//        // Look if they're allowed to hold all block and item data in arrays
//        // I am also allowed to abuse maps to extend the metadata excuse
//        // doors have ok ? I don't even care about the overhead at this point.
//        public static final HashMap<Integer, Long> DOOR_UPDATES = new HashMap<>();
//        public static final HashMap<Integer, Boolean> DOOR_STATES = new HashMap<>();
    }
}
