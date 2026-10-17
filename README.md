# Portable Stereo (Fabric, Minecraft 26.3)

A boombox you carry around. Load any music disc and everyone within jukebox range (~64 blocks)
hears it, and the sound follows you as you move.

## How to use
- Craft: iron ingots across the top and bottom, note block / jukebox / note block in the middle.
  (Also in the Tools & Utilities creative tab.)
- Hold the stereo in one hand and a music disc in the other, then right-click: loads the disc and plays it.
- Right-click again: stop / play.
- Sneak + right-click: eject the disc.
- Music stops automatically when the song ends, you die, or the stereo leaves your inventory.
- Volume is controlled by the vanilla "Jukebox/Note Blocks" slider.

## Building
Requires JDK 25.

1. Open the folder in IntelliJ IDEA (it will import the Gradle project), or
2. From a terminal with Gradle 9.6+ installed, generate the wrapper once: `gradle wrapper`
   then build: `./gradlew build` (Windows: `gradlew.bat build`)

The mod jar lands in `build/libs/portable-stereo-1.0.0.jar`. Drop it in your `mods` folder
alongside Fabric API. It must be installed on the server AND on every client.

To test in a dev client: `./gradlew runClient`
