package com.iridium.chunkbusters.support;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Default implements Support {
    @Override
    public boolean canDelete(Player player, Location location) {
        return true;
    }

    @Override
    public boolean sameFaction(UUID uuid, UUID other) {
        return uuid.equals(other);
    }
}
