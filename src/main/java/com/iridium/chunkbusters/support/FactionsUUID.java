package com.iridium.chunkbusters.support;

import com.iridium.chunkbusters.database.ChunkBuster;
import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionsUUID implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        FLocation loc = new FLocation(location);
        Faction B = Board.getInstance().getFactionAt(loc);
        return (ChatColor.stripColor(B.getTag()).equalsIgnoreCase("Wilderness")) || (faction.equals(B));
    }

    @Override
    public boolean isRelevant(UUID uuid, ChunkBuster chunkBuster) {
        Chunk chunk = chunkBuster.getChunk();
        Location location = new Location(chunk.getWorld(), chunk.getX() << 4, 0, chunk.getZ() << 4);
        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
        if (fPlayer.getFaction() == null) {
            return uuid.equals(chunkBuster.getUuid());
        }
        return fPlayer.getFaction().equals(Board.getInstance().getFactionAt(new FLocation(location)));
    }
}
