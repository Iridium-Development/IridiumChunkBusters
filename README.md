# IridiumChunkBusters
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b4024c5efc094efaa5561762c3566bb6)](https://www.codacy.com/gh/Iridium-Development/IridiumChunkBusters/dashboard)

_Changing the world, one chunk at a time._

This plugin provides players with a "chunkbuster," an item that, when used, automatically mines all of the blocks within a traditional 16x16 chunk.

Includes:
- Blacklist: chunkbusters will not break these blocks (bedrock & spawners by default)
- Logs: can see who used a chunkbuster and undo deletion of a chunk

IridiumChunkbuster, as with all of Iridium Development's software, aims to deliver a premium image, streamlining the setup process and eliminating the headache of having to figure out the details, while also proving to be extremely configurable to create an unparalleled experience.

# Getting Started

Download the plugin from ~~Spigot, Modrinth, Hangar,~~ Github Releases, or compile it yourself.

Once you have a copy of the plugin (it should be a `.jar` file), simply place it in the `server/plugins` folder.

# Compiling

Clone the repo, and run the `build.gradle.kts` script with `gradle build`.

# Developing

You may notice when compiling and developing against IridiumChunkbusters that there is a significant portion of code that isn't located in this repo. That's because IridiumChunkbusters uses functions from IridiumCore.

- IridiumCore
  - A sort of library for all of Iridium Development's plugins

- IridiumChunkbusters
  - This plugin, which extends IridiumCore, and houses its own code specific to chunkbusters, such as chunk deletion.

# Support

If you think you've found a bug, please make sure you isolate the issue down to IridiumChunkbusters before posting an issue in our [Issues](https://github.com/Iridium-Development/IridiumChunkbusters/issues) tab. While you're there, please follow our issues guidelines.

If you encounter any issues while using the plugin, feel free to join our support [Discord](https://discord.gg/6HJ73mWE7P).