# InventoryTweaks StationAPI Edition for Minecraft Beta 1.7.3

A StationAPI mod for Minecraft Beta 1.7.3 that adds some inventory tweaks.

# Inventory Tweaks

This mod is incompatible with the regular version of MojangFix. If you would like to use that mod you will need the compatibility version of the mod for InventoryTweaks found here: [https://github.com/telvarost/MojangFix-StationAPI/releases](https://github.com/telvarost/MojangFix-StationAPI/releases)

**This version of the mod now supports multiplayer!**
* Features on multiplayer are slightly simplified to accommodate what vanilla servers are capable of.

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
    * Disabled by default in v2.3.1+
* MouseTweaks `Left-Click + Drag` (with item)
    * Lets you quickly pick up or move items of the same type.
    * Move items to another inventory if holding `Shift`
* MouseTweaks `Left-Click + Drag + Shift` (without item)
    * Quickly move items into another inventory.
* Original take on the scroll wheel
  * Scroll to move items between the cursor and the hovered slot
  * Note: MouseTweaks scroll will eventually be added as well if I can figure it out

## Installation using Prism Launcher

1. Download an instance of Babric for Prism Launcher: https://github.com/Glass-Series/babric-prism-instance
2. Install Java 17 and set the instance to use it: https://adoptium.net/temurin/releases/
3. Add GlassConfigAPI 3.0.2+ to the mod folder for the instance: https://modrinth.com/mod/glass-config-api
4. Add Glass Networking to the mod folder for the instance: https://modrinth.com/mod/glass-networking
5. (Optional) Add StationAPI to the mod folder for the instance: https://modrinth.com/mod/stationapi
6. (Optional) Add Mod Menu to the mod folder for the instance: https://modrinth.com/mod/modmenu-beta
7. Add this mod to the mod folder for the instance: https://github.com/telvarost/BetaTweaks-StationAPI/releases
8. Run and enjoy! 👍

## Feedback

Got any suggestions on what should be added next? Feel free to share it by [creating an issue](https://github.com/telvarost/InventoryTweaks-StationAPI/issues/new). Know how to code and want to do it yourself? Then look below on how to get started.

## Contributing

Thanks for considering contributing! To get started fork this repository, make your changes, and create a PR. 

If you are new to StationAPI consider watching the following videos on Babric/StationAPI Minecraft modding: https://www.youtube.com/watch?v=9-sVGjnGJ5s&list=PLa2JWzyvH63wGcj5-i0P12VkJG7PDyo9T
