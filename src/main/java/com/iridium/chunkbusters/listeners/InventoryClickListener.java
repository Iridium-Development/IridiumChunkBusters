package com.iridium.chunkbusters.listeners;

import com.iridium.chunkbusters.ChunkManager;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.gui.ConfirmationGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        InventoryHolder inventoryHolder = event.getClickedInventory().getHolder();
        if (inventoryHolder == null) return;
        if (inventoryHolder instanceof ConfirmationGUI) {
            ConfirmationGUI confirmationGUI = (ConfirmationGUI) inventoryHolder;
            event.setCancelled(true);
            if (event.getSlot() == 15) {
                ChunkManager.deleteChunks((Player) event.getWhoClicked(), confirmationGUI.getLocation().getChunk(), IridiumChunkBusters.getInstance().getConfiguration().startYWherePlaced ? confirmationGUI.getLocation().getBlockY() : confirmationGUI.getLocation().getWorld().getMaxHeight(), confirmationGUI.getSize());
                confirmationGUI.setActivated(true);
                event.getWhoClicked().closeInventory();
            } else if (event.getSlot() == 11) {
                confirmationGUI.getLocation().getBlock().setType(Material.AIR, false);
                event.getWhoClicked().getInventory().addItem(ChunkManager.getChunkBuster(confirmationGUI.getSize()));
                event.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof ConfirmationGUI) {
            ConfirmationGUI confirmationGUI = (ConfirmationGUI) inventoryHolder;
            if (confirmationGUI.isActivated()) return;
            confirmationGUI.getLocation().getBlock().setType(Material.AIR, false);
            event.getPlayer().getInventory().addItem(ChunkManager.getChunkBuster(confirmationGUI.getSize()));
        }
    }

}
