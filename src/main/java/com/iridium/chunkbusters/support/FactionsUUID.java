package com.iridium.chunkbusters.support;

import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionsUUID implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        FLocation loc = new FLocation(location);
        Faction B = Board.getInstance().getFactionAt(loc);
        return (ChatColor.stripColor(B.getTag()).equalsIgnoreCase("Wilderness")) || (faction == B);
    }

    @Override
    public boolean sameFaction(UUID uuid, UUID other) {
        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
        if (fPlayer.getFaction() == null) {
            return uuid.equals(other);
        }
        return fPlayer.getFaction().equals(FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(other)).getFaction());
    }
}
