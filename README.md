# InventoryTweaks StationAPI Edition for Minecraft Beta 1.7.3

A StationAPI mod for Minecraft Beta 1.7.3 that adds some inventory tweaks.

# Inventory Tweaks

This mod is currently incompatible with the regular version of MojangFix. If you would like to use that mod you will need the compatibility version of the mod for InventoryTweaks found here: [https://github.com/telvarost/MojangFix-StationAPI/releases](https://github.com/telvarost/MojangFix-StationAPI/releases)

**There's a very high chance this mod will not work in multiplayer, as of right now it is purely for singleplayer**

## List of Changes:

* Note that all changes can be enabled/disabled in the config menu if you have ModMenu and GlassConfigAPI.

### Modern Minecraft Changes
* Adds `Left-Click + Drag` mechanic to evenly distribute held items over empty slots/slots with the same item as in modern Minecraft.
* Adds `Right-Click + Drag` mechanic to distribute one item from held items over empty slots/slots with the same item as in modern Minecraft.
* `LCtrl + DROP_KEY` to drop a whole stack of items.
* Move items from player inventory to the hotbar by pressing the number key corresponding to the desired hotbar slot while hovering the cursor over the item to move.

### MouseTweaks Changes
* MouseTweaks `Right-Click + Drag`
    * Very similar to the standard RMB dragging mechanic, with one difference: if you drag over a slot multiple times, an item will be put there multiple times. Replaces the standard mechanic if enabled.
* MouseTweaks `Left-Click + Drag` (with item)
    * Lets you quickly pick up or move items of the same type.
    * Move items to another inventory if holding `Shift`
* MouseTweaks `Left-Click + Drag + Shift` (without item)
    * Quickly move items into another inventory.

## Installation using Prism Launcher

1. Download an instance of Babric for Prism Launcher: https://github.com/babric/prism-instance
2. Install Java 17, set the instance to use it, and disable compatibility checks on the instance: https://adoptium.net/temurin/releases/
3. Add StationAPI to the mod folder for the instance: https://jenkins.glass-launcher.net/job/StationAPI/lastSuccessfulBuild/
4. (Optional) Add Mod Menu to the mod folder for the instance: https://github.com/calmilamsy/ModMenu/releases
5. (Optional) Add GlassConfigAPI 1.1.6+ to the mod folder for the instance: https://maven.glass-launcher.net/#/releases/net/glasslauncher/mods/GlassConfigAPI
6. Add this mod to the mod folder for the instance: https://github.com/telvarost/InventoryTweaks-StationAPI/releases
7. Run and enjoy! 👍

## Feedback

Got any suggestions on what should be added next? Feel free to share it by [creating an issue](https://github.com/telvarost/InventoryTweaks-StationAPI/issues/new). Know how to code and want to do it yourself? Then look below on how to get started.

## Contributing

Thanks for considering contributing! To get started fork this repository, make your changes, and create a PR. 

If you are new to StationAPI consider watching the following videos on Babric/StationAPI Minecraft modding: https://www.youtube.com/watch?v=9-sVGjnGJ5s&list=PLa2JWzyvH63wGcj5-i0P12VkJG7PDyo9T
