package com.iridium.chunkbusters.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.ChunkBuster;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.utils.ItemStackUtils;
import com.iridium.chunkbusters.utils.Placeholder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class LogsGUI implements InventoryHolder {

    private final UUID uuid;
    private int page;

    private boolean next;
    private boolean previous;

    public LogsGUI(UUID uuid, int page) {
        this.uuid = uuid;
        this.page = page;
        this.next = false;
        this.previous = page > 1;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "Chunk Busters");
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
        }
        IridiumChunkBusters.getInstance().getDatabaseManager().getChunkBusters().thenAccept(list -> {
            List<ChunkBuster> chunkBusters = list.stream().filter(chunkBuster -> IridiumChunkBusters.getInstance().getSupport().sameFaction(this.uuid, chunkBuster.getUuid())).collect(Collectors.toList());
            int i = 0;
            int slot = 0;
            for (ChunkBuster chunkBuster : chunkBusters) {
                if (i >= 45 * (page - 1) && i < 45 * page) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(chunkBuster.getUuid());
                    String chunk = chunkBuster.getChunk().getWorld().getName() + " " + chunkBuster.getChunk().getX() + "," + chunkBuster.getChunk().getZ();
                    inventory.setItem(slot, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().chunkBusterLog, Arrays.asList(
                            new Placeholder("player", player.getName()),
                            new Placeholder("radius", String.valueOf(chunkBuster.getRadius())),
                            new Placeholder("time", chunkBuster.getTime().format(DateTimeFormatter.ofPattern(IridiumChunkBusters.getInstance().getConfiguration().dateTimeFormat))),
                            new Placeholder("chunk", chunk)
                    )));
                    slot++;
                }
                i++;
            }
            inventory.setItem(48, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().previousPage));
            inventory.setItem(51, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().nextPage));
            next = 45 * page < chunkBusters.size();
        });
        return inventory;
    }
}