package com.iridium.chunkbusters.database;

import com.iridium.chunkbusters.ChunkLayer;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.gui.ConfirmationGUI;
import com.cryptomorin.xseries.XMaterial;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "chunkbusters")
public class ChunkBuster {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    @NotNull
    private Integer id;

    @DatabaseField(columnName = "uuid", canBeNull = false)
    @NotNull
    private UUID uuid;

    @DatabaseField(columnName = "chunk", canBeNull = false)
    @NotNull
    private String chunk;

    @DatabaseField(columnName = "radius", canBeNull = false)
    private int radius;

    @DatabaseField(columnName = "time", canBeNull = false)
    @NotNull
    private Long time;

    @DatabaseField(columnName = "y_level", canBeNull = false)
    private int y;

    @DatabaseField(columnName = "starting_level", canBeNull = false)
    private int startingLevel;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<BlockData> blockDataList;

    public LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public Chunk getChunk() {
        String[] coords = chunk.split(",");
        World world = Bukkit.getWorld(coords[0]);
        return world.getChunkAt(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
    }

    public ChunkBuster(@NotNull UUID uuid, @NotNull Chunk chunk, int radius, int y) {
        this.uuid = uuid;
        this.chunk = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
        this.radius = radius;
        this.time = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.y = y;
        this.startingLevel = y;
    }

    public void deleteChunks() {
        Chunk c = getChunk();
        int cx = c.getX();
        int cz = c.getZ();
        List<Chunk> chunks = new ArrayList<>();
        for (int x = cx - (radius - 1); x <= cx + (radius - 1); x++) {
            for (int z = cz - (radius - 1); z <= cz + (radius - 1); z++) {
                Chunk chunk = c.getWorld().getChunkAt(x, z);
                chunks.add(chunk);
            }
        }
        int minheight = XMaterial.getVersion() >= 17 ? c.getWorld().getMinHeight() : 0;
        Bukkit.getScheduler().runTask(IridiumChunkBusters.getInstance(), () -> deleteChunks(chunks, minheight));
        IridiumChunkBusters.getInstance().getActiveChunkBusters().add(this);
    }

    private void deleteChunks(final List<Chunk> chunks, int minHeight) {
        Player player = Bukkit.getPlayer(uuid);
        if (y == minHeight) {
            for (Chunk c : chunks) {
                IridiumChunkBusters.getInstance().getNms().sendChunk(c.getWorld().getPlayers(), c);
            }
            Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> IridiumChunkBusters.getInstance().getDatabaseManager().saveChunkBuster(this));
            IridiumChunkBusters.getInstance().getActiveChunkBusters().remove(this);
            return;
        }
        if (player != null) {
//            IridiumChunkBusters.getInstance().getNms().sendActionBar(player, StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().actionBarMessage.replace("{ylevel}", String.valueOf(y))));
        }
        HashSet<Location> chunkBusters = IridiumChunkBusters.getInstance().getConfirmationGUIS().stream().map(ConfirmationGUI::getLocation).collect(Collectors.toCollection(HashSet::new));
        for (Chunk c : chunks) {
            ChunkLayer chunkLayer = new ChunkLayer();
            int cx = c.getX() << 4;
            int cz = c.getZ() << 4;

            World world = c.getWorld();

            for (int x = cx; x < cx + 16; x++) {
                for (int z = cz; z < cz + 16; z++) {
                    Location location = new Location(world, x, y, z);
                    BlockState blockState = location.getBlock().getState();
                    if (IridiumChunkBusters.getInstance().getConfiguration().blacklist.contains(XMaterial.matchXMaterial(blockState.getType())) || !IridiumChunkBusters.getInstance().getSupport().canDelete(player, location) || blockState.getType().equals(Material.AIR) || chunkBusters.contains(location)) {
                        continue;
                    }
                    chunkLayer.blocks[x - cx][z - cz] = blockState.getType();
                    chunkLayer.data[x - cx][z - cz] = blockState.getRawData();
                    IridiumChunkBusters.getInstance().getNms().deleteBlockFast(location);
                }
            }
            IridiumChunkBusters.getInstance().getNms().sendChunk(c.getWorld().getPlayers(), c);
            IridiumChunkBusters.getInstance().getDatabaseManager().saveBlockData(new BlockData(this, world.getName(), c.getX(), y, c.getZ(), chunkLayer));
        }
        y--;
        if (IridiumChunkBusters.getInstance().getConfiguration().deleteInterval < 1) {
            deleteChunks(chunks, minHeight);
        } else {
            Bukkit.getScheduler().runTaskLater(IridiumChunkBusters.getInstance(), () -> deleteChunks(chunks, minHeight), IridiumChunkBusters.getInstance().getConfiguration().deleteInterval);
        }
    }

    public void undo() {
        Player player = Bukkit.getPlayer(uuid);
        if (y > startingLevel) {
            Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> IridiumChunkBusters.getInstance().getDatabaseManager().deleteChunkBuster(this));
            return;
        }
        if (player != null) {
//            IridiumChunkBusters.getInstance().getNms().sendActionBar(player, StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().actionBarMessage.replace("{ylevel}", String.valueOf(y))));
        }
        blockDataList.stream().filter(bd -> bd.getY() == y).forEach(blockData -> {
            Chunk chunk = blockData.getChunk();
            ChunkLayer chunkLayer = blockData.getBlocks();
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (chunkLayer.blocks[x][z] == null) continue;
                    BlockState blockState = chunk.getBlock(x, y, z).getState();
                    if (IridiumChunkBusters.getInstance().getConfiguration().onlyRestoreWhenBlockIsAir && !blockState.getType().equals(Material.AIR))
                        continue;
                    blockState.setType(chunkLayer.blocks[x][z]);
                    blockState.setRawData(chunkLayer.data[x][z]);
                    blockState.update(true, false);
                }
            }
        });
        y++;
        if (IridiumChunkBusters.getInstance().getConfiguration().deleteInterval < 1) {
            undo();
        } else {
            Bukkit.getScheduler().runTaskLater(IridiumChunkBusters.getInstance(), this::undo, IridiumChunkBusters.getInstance().getConfiguration().deleteInterval);
        }
    }

}
