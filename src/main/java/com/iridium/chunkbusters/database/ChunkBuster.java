package com.iridium.chunkbusters.database;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.utils.StringUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

    @DatabaseField(columnName = "ylevel", canBeNull = false)
    private int y;

    public LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public Chunk getChunk() {
        String[] coords = chunk.split(",");
        World world = Bukkit.getWorld(coords[0]);
        return world.getChunkAt(Integer.valueOf(coords[1]), Integer.valueOf(coords[2]));
    }

    public ChunkBuster(@NotNull UUID uuid, @NotNull Chunk chunk, int radius, int y) {
        this.uuid = uuid;
        this.chunk = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
        this.radius = radius;
        this.time = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.y = y;
    }

    public void deleteChunks() {
        Chunk c = getChunk();
        int cx = c.getX();
        int cz = c.getZ();
        HashMap<Chunk, ChunkSnapshot> chunks = new HashMap<>();
        for (int x = cx - (radius - 1); x <= cx + (radius - 1); x++) {
            for (int z = cz - (radius - 1); z <= cz + (radius - 1); z++) {
                Chunk chunk = c.getWorld().getChunkAt(x, z);
                chunks.put(chunk, chunk.getChunkSnapshot());
            }
        }
        deleteChunks(chunks);
        Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> IridiumChunkBusters.getInstance().getDatabaseManager().saveChunkBuster(this));
        IridiumChunkBusters.getInstance().getActiveChunkBusters().add(this);
    }

    private void deleteChunks(final HashMap<Chunk, ChunkSnapshot> chunks) {
        Player player = Bukkit.getPlayer(uuid);
        if (y == 0) {
            if (player != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.color("")));
            }
            for (Chunk c : chunks.keySet()) {
                IridiumChunkBusters.getInstance().getNms().sendChunk(c, c.getWorld().getPlayers());
            }
            Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> IridiumChunkBusters.getInstance().getDatabaseManager().saveChunkBuster(this));
            IridiumChunkBusters.getInstance().getActiveChunkBusters().remove(this);
            return;
        }
        if (player != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().actionBarMessage.replace("{ylevel}", String.valueOf(y)))));
        }

        for (Chunk c : chunks.keySet()) {
            List<Location> tileEntities = Arrays.stream(c.getTileEntities()).map(BlockState::getLocation).collect(Collectors.toList());
            ChunkSnapshot chunkSnapshot = chunks.get(c);
            int cx = c.getX() << 4;
            int cz = c.getZ() << 4;

            List<Location> changedBlocks = new ArrayList<>();

            World world = c.getWorld();

            for (int x = cx; x < cx + 16; x++) {
                for (int z = cz; z < cz + 16; z++) {
                    Material material = chunkSnapshot.getBlockType(x - cx, y, z - cz);
                    Location location = new Location(world, x, y, z);
                    changedBlocks.add(location);
                    if (!IridiumChunkBusters.getInstance().getConfiguration().blacklist.contains(XMaterial.matchXMaterial(material))) {
                        if (IridiumChunkBusters.getInstance().getSupport().canDelete(player, location)) {
                            if (tileEntities.contains(location)) {
                                //NMS will throw errors when trying to delete a Tile Entity
                                location.getBlock().setType(Material.AIR, false);
                            } else {
                                IridiumChunkBusters.getInstance().getNms().setBlockFast(c.getWorld(), x, y, z, 0, (byte) 0, false);
                            }
                        }
                    }
                }
            }
            IridiumChunkBusters.getInstance().getNms().sendChunk(c, changedBlocks, c.getWorld().getPlayers());
        }
        y--;
        if (IridiumChunkBusters.getInstance().getConfiguration().deleteInterval < 1) {
            deleteChunks(chunks);
        } else {
            Bukkit.getScheduler().runTaskLater(IridiumChunkBusters.getInstance(), () -> deleteChunks(chunks), IridiumChunkBusters.getInstance().getConfiguration().deleteInterval);
        }
    }

}
