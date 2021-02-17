package com.iridium.chunkbusters.support;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Support {
    boolean canDelete(Player player, Location location);
    boolean sameFaction(UUID uuid, UUID other);
}
