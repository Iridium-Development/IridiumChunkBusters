package com.iridium.chunkbusters.support;

import com.iridium.chunkbusters.database.ChunkBuster;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Support {
    boolean canDelete(Player player, Location location);

    boolean isRelevant(UUID uuid, ChunkBuster chunkBuster);
}
