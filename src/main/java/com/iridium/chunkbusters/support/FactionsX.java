package com.iridium.chunkbusters.support;

import com.iridium.chunkbusters.database.ChunkBuster;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionsX implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(player);
        return fPlayer.canBreakAt(location);
    }

    @Override
    public boolean isRelevant(UUID uuid, ChunkBuster chunkBuster) {
        Chunk chunk = chunkBuster.getChunk();
        Location location = new Location(chunk.getWorld(), chunk.getX() << 4, 0, chunk.getZ() << 4);
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(uuid);
        return fPlayer.canBuildAt(location);
    }
}
