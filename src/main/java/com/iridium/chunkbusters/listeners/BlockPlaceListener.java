package com.iridium.chunkbusters.listeners;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.gui.ConfirmationGUI;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        int amount = IridiumChunkBusters.getInstance().getChunkBusterSize(event.getItemInHand());
        if (amount < 1) return;
        Location location = event.getBlock().getLocation();
        ConfirmationGUI confirmationGUI = new ConfirmationGUI(amount, location);
        event.getPlayer().openInventory(confirmationGUI.getInventory());
    }

}
