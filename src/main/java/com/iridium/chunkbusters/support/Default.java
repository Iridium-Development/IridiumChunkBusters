package com.iridium.chunkbusters.support;

import com.iridium.chunkbusters.database.ChunkBuster;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Default implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        return true;
    }

    @Override
    public boolean isRelevant(UUID uuid, ChunkBuster chunkBuster) {
        return uuid.equals(chunkBuster.getUuid());
    }
}
