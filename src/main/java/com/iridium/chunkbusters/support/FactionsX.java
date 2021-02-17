package com.iridium.chunkbusters.support;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.manager.PlayerManager;
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
    public boolean sameFaction(UUID uuid, UUID other) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(uuid);
        if (fPlayer.getFaction() == null) {
            return uuid.equals(other);
        }
        return fPlayer.getFaction().equals(PlayerManager.INSTANCE.getFPlayer(other).getFaction());
    }
}
