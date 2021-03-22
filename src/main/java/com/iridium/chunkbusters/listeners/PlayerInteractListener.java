package com.iridium.chunkbusters.listeners;

import com.iridium.chunkbusters.IridiumChunkBusters;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            boolean isChunkBuster = IridiumChunkBusters.getInstance().getConfirmationGUIS().stream().anyMatch(confirmationGUI -> confirmationGUI.getLocation().equals(event.getClickedBlock().getLocation()));
            if (isChunkBuster) {
                event.setCancelled(true);
            }
        }
    }

}
