package com.iridium.chunkbusters.support;


import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Factions implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        Faction P = MPlayer.get(player).getFaction();
        Faction B = BoardColl.get().getFactionAt(PS.valueOf(location));
        return (ChatColor.stripColor(B.getName()).equalsIgnoreCase("Wilderness")) || (P == B);
    }

    @Override
    public boolean sameFaction(UUID uuid, UUID other) {
        Faction faction = MPlayer.get(uuid).getFaction();
        if (faction == null) {
            return uuid.equals(other);
        }
        return faction.equals(MPlayer.get(other).getFaction());
    }
}
