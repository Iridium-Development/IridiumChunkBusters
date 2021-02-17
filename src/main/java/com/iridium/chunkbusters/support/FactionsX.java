package com.iridium.chunkbusters.support;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsX implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(player);
        return fPlayer.canBreakAt(location);
    }
}
