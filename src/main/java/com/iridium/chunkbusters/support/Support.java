package com.iridium.chunkbusters.support;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Support {
    boolean canDelete(Player player, Block block);
}
