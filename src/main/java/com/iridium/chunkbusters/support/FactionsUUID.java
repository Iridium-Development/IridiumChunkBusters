package com.iridium.chunkbusters.support;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsUUID implements Support {
    @Override
    public boolean canDelete(Player player, Block block) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        FLocation loc = new FLocation(block.getLocation());
        Faction B = Board.getInstance().getFactionAt(loc);
        return (ChatColor.stripColor(B.getTag()).equalsIgnoreCase("Wilderness")) || (faction == B);
    }
}
