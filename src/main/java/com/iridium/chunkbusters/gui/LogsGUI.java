package com.iridium.chunkbusters.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.database.ChunkBuster;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class LogsGUI implements InventoryHolder {

    private final UUID uuid;
    private final int page;

    private boolean next;
    private final boolean previous;

    private final HashMap<Integer, ChunkBuster> chunkBusters = new HashMap<>();

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
            List<ChunkBuster> chunkBustersList = list.stream().filter(chunkBuster -> IridiumChunkBusters.getInstance().getSupport().isRelevant(this.uuid, chunkBuster)).collect(Collectors.toList());
            int i = 0;
            int slot = 0;
            for (ChunkBuster chunkBuster : chunkBustersList) {
                if (i >= 45 * (page - 1) && i < 45 * page) {
                    chunkBusters.put(slot, chunkBuster);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(chunkBuster.getUuid());
                    String chunk = chunkBuster.getChunk().getWorld().getName() + " " + chunkBuster.getChunk().getX() + "," + chunkBuster.getChunk().getZ();
                    inventory.setItem(slot, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().chunkBusterLog, Arrays.asList(
                            new Placeholder("player", player.getName()),
                            new Placeholder("size", String.valueOf(chunkBuster.getRadius() * 2 - 1)),
                            new Placeholder("time", chunkBuster.getTime().format(DateTimeFormatter.ofPattern(IridiumChunkBusters.getInstance().getConfiguration().dateTimeFormat))),
                            new Placeholder("chunk", chunk)
                    )));
                    slot++;
                }
                i++;
            }
            inventory.setItem(48, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().previousPage));
            inventory.setItem(51, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().nextPage));
            next = 45 * page < chunkBustersList.size();
        });
        return inventory;
    }
}