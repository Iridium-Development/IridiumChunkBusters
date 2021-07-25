package com.iridium.chunkbusters.support;

import com.iridium.chunkbusters.database.ChunkBuster;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Factions implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        Faction P = MPlayer.get(player).getFaction();
        Faction B = BoardColl.get().getFactionAt(PS.valueOf(location));
        return (ChatColor.stripColor(B.getName()).equalsIgnoreCase("Wilderness")) || (P.equals(B));
    }

    @Override
    public boolean isRelevant(UUID uuid, ChunkBuster chunkBuster) {
        Chunk chunk = chunkBuster.getChunk();
        Faction faction = MPlayer.get(uuid).getFaction();
        if (faction == null) {
            return uuid.equals(chunkBuster.getUuid());
        }
        return BoardColl.get().getFactionAt(PS.valueOf(chunk)).equals(faction);
    }
}
