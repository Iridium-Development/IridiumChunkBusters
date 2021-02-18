package com.iridium.chunkbusters.listeners;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.database.ChunkBuster;
import com.iridium.chunkbusters.gui.ConfirmationGUI;
import com.iridium.chunkbusters.gui.LogsGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Optional;

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
                ChunkBuster chunkBuster = new ChunkBuster(event.getWhoClicked().getUniqueId(), confirmationGUI.getLocation().getChunk(), confirmationGUI.getSize(), IridiumChunkBusters.getInstance().getConfiguration().startYWherePlaced ? confirmationGUI.getLocation().getBlockY() : confirmationGUI.getLocation().getWorld().getMaxHeight());
                confirmationGUI.getLocation().getBlock().setType(Material.AIR, false);

                //We need to save the chunkBuster to the db first so the id gets generated so its not null
                Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> {
                    IridiumChunkBusters.getInstance().getDatabaseManager().saveChunkBuster(chunkBuster);
                    IridiumChunkBusters.getInstance().getDatabaseManager().getChunkBusters().thenAccept(chunkBusters -> {
                        Optional<ChunkBuster> chunkBusterOptional = chunkBusters.stream().filter(cb -> cb.getTime().equals(chunkBuster.getTime()) && cb.getChunk().equals(chunkBuster.getChunk())).findFirst();
                        chunkBusterOptional.ifPresent(buster -> Bukkit.getScheduler().runTask(IridiumChunkBusters.getInstance(), buster::deleteChunks));
                    });
                });

                confirmationGUI.setActivated(true);
                event.getWhoClicked().closeInventory();
            } else if (event.getSlot() == 11) {
                confirmationGUI.getLocation().getBlock().setType(Material.AIR, false);
                event.getWhoClicked().getInventory().addItem(IridiumChunkBusters.getInstance().getChunkBuster(confirmationGUI.getSize()));
                event.getWhoClicked().closeInventory();
            }
        } else if (inventoryHolder instanceof LogsGUI) {
            LogsGUI logsGUI = (LogsGUI) inventoryHolder;
            event.setCancelled(true);
            if (logsGUI.getChunkBusters().containsKey(event.getSlot())) {
                ChunkBuster chunkBuster = logsGUI.getChunkBusters().get(event.getSlot());
                if (chunkBuster.getY() == 0 && IridiumChunkBusters.getInstance().getConfiguration().restoreChunkBusters) {
                    chunkBuster.undo();
                    Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> IridiumChunkBusters.getInstance().getDatabaseManager().deleteChunkBuster(chunkBuster));
                    event.getWhoClicked().closeInventory();
                }
            } else if (event.getSlot() == 51 && logsGUI.isNext()) {
                event.getWhoClicked().openInventory(new LogsGUI(logsGUI.getUuid(), logsGUI.getPage() + 1).getInventory());
            } else if (event.getSlot() == 48 && logsGUI.isPrevious()) {
                event.getWhoClicked().openInventory(new LogsGUI(logsGUI.getUuid(), logsGUI.getPage() - 1).getInventory());
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
            event.getPlayer().getInventory().addItem(IridiumChunkBusters.getInstance().getChunkBuster(confirmationGUI.getSize()));
        }
    }

}
