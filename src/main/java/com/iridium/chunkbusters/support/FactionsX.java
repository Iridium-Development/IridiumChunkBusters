package com.iridium.chunkbusters.support;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsX implements Support {
    @Override
    public boolean canDelete(Player player, Block block) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(player);
        return fPlayer.canBreakAt(block.getLocation());
    }
}
