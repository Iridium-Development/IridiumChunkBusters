package com.iridium.chunkbusters.nms;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface NMS {
    void setBlockFast(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics);

    void sendChunk(Chunk chunk, List<Location> blocks, List<Player> players);

    void sendChunk(Chunk chunk, List<Player> players);

    void sendActionBar(Player player, String message);
}
