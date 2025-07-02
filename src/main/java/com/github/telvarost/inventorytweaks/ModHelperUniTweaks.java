package com.github.telvarost.inventorytweaks;

import net.danygames2014.unitweaks.tweaks.morekeybinds.KeyBindingListener;
import org.lwjgl.input.Keyboard;

public class ModHelperUniTweaks {
    public static int remapKeyCodes(int keyCode) {

        if (keyCode == KeyBindingListener.hotbar1.code) {
            keyCode = Keyboard.KEY_1;
        } else if (keyCode == KeyBindingListener.hotbar2.code) {
            keyCode = Keyboard.KEY_2;
        } else if (keyCode == KeyBindingListener.hotbar3.code) {
            keyCode = Keyboard.KEY_3;
        } else if (keyCode == KeyBindingListener.hotbar4.code) {
            keyCode = Keyboard.KEY_4;
        } else if (keyCode == KeyBindingListener.hotbar5.code) {
            keyCode = Keyboard.KEY_5;
        } else if (keyCode == KeyBindingListener.hotbar6.code) {
            keyCode = Keyboard.KEY_6;
        } else if (keyCode == KeyBindingListener.hotbar7.code) {
            keyCode = Keyboard.KEY_7;
        } else if (keyCode == KeyBindingListener.hotbar8.code) {
            keyCode = Keyboard.KEY_8;
        } else if (keyCode == KeyBindingListener.hotbar9.code) {
            keyCode = Keyboard.KEY_9;
        } else {
            keyCode = Keyboard.KEY_0;
        }

        return keyCode;
    }
}
