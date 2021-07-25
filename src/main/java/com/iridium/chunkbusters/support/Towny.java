package com.iridium.chunkbusters.support;

import com.iridium.chunkbusters.database.ChunkBuster;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Towny implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {

        TownyWorld world = TownyUniverse.getInstance().getWorldMap().get(location.getWorld().getName());
        if (world == null) {
            return true;
        }
        if (TownyAPI.getInstance().isWilderness(location)) {
            return true;
        }
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), TownyPermission.ActionType.DESTROY);
    }

    @Override
    public boolean isRelevant(UUID uuid, ChunkBuster chunkBuster) {
        return uuid.equals(chunkBuster.getUuid());
    }
}
