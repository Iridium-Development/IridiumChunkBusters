package com.iridium.chunkbusters.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.database.ChunkBuster;
import com.iridium.chunkbusters.utils.ItemStackUtils;
import com.iridium.chunkbusters.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfirmationGUI implements GUI {

    private final int size;
    private final Location location;
    private boolean activated;

    public ConfirmationGUI(int size, Location location) {
        this.size = size;
        this.location = location;
        this.activated = false;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().confirmationGUITitle));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
        }
        inventory.setItem(11, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().no));
        inventory.setItem(15, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().yes));
        return inventory;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == 15) {
            ChunkBuster chunkBuster = new ChunkBuster(event.getWhoClicked().getUniqueId(), location.getChunk(), size, IridiumChunkBusters.getInstance().getConfiguration().startYWherePlaced ? location.getBlockY() : location.getWorld().getMaxHeight());
            location.getBlock().setType(Material.AIR, false);

            //We need to save the chunkBuster to the db first so the id gets generated so its not null
            Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> {
                IridiumChunkBusters.getInstance().getDatabaseManager().saveChunkBuster(chunkBuster);
                IridiumChunkBusters.getInstance().getDatabaseManager().getChunkBusters().thenAccept(chunkBusters -> {
                    Optional<ChunkBuster> chunkBusterOptional = chunkBusters.stream().filter(cb -> cb.getTime().equals(chunkBuster.getTime()) && cb.getChunk().equals(chunkBuster.getChunk())).findFirst();
                    chunkBusterOptional.ifPresent(buster -> Bukkit.getScheduler().runTask(IridiumChunkBusters.getInstance(), buster::deleteChunks));
                });
            });

            this.activated = true;
            event.getWhoClicked().closeInventory();
        } else if (event.getSlot() == 11) {
            location.getBlock().setType(Material.AIR, false);
            event.getWhoClicked().getInventory().addItem(IridiumChunkBusters.getInstance().getChunkBuster(size));
            event.getWhoClicked().closeInventory();
        }
    }
}
