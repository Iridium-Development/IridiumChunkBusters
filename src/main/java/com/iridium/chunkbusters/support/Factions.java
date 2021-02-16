package com.iridium.chunkbusters.support;


import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Factions implements Support {
    @Override
    public boolean canDelete(Player player, Block block) {
        Faction P = MPlayer.get(player).getFaction();
        Faction B = BoardColl.get().getFactionAt(PS.valueOf(block.getLocation()));
        return (ChatColor.stripColor(B.getName()).equalsIgnoreCase("Wilderness")) || (P == B);
    }
}
