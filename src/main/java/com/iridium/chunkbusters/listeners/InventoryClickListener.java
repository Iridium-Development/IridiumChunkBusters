package com.iridium.chunkbusters.listeners;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.gui.ConfirmationGUI;
import com.iridium.chunkbusters.gui.GUI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null
                && event.getInventory().getHolder() != null
                && event.getInventory().getHolder() instanceof GUI) {
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof ConfirmationGUI) {
            ConfirmationGUI confirmationGUI = (ConfirmationGUI) inventoryHolder;
            IridiumChunkBusters.getInstance().getConfirmationGUIS().remove(confirmationGUI);
            if (confirmationGUI.isClickAction()) return;
            confirmationGUI.setClickAction(true);
            confirmationGUI.getLocation().getBlock().setType(Material.AIR, false);
            event.getPlayer().getInventory().addItem(IridiumChunkBusters.getInstance().getChunkBuster(confirmationGUI.getSize()));
        }
    }
}
