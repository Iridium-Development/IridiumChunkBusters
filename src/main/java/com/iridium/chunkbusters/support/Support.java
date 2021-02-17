package com.iridium.chunkbusters.support;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Support {
    boolean canDelete(Player player, Location location);
}
