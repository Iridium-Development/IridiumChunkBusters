package com.iridium.chunkbusters.database;

import com.iridium.chunkbusters.ChunkLayer;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "blockdata")
public class BlockData {
    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    @NotNull
    private Integer id;

    @DatabaseField(columnName = "chunkbuster", canBeNull = false, foreign = true)
    @NotNull
    private ChunkBuster chunkBuster;

    @DatabaseField(columnName = "world", canBeNull = false)
    @NotNull
    private String world;

    @DatabaseField(columnName = "y", canBeNull = false)
    @NotNull
    private int y;

    @DatabaseField(columnName = "chunk_x", canBeNull = false)
    @NotNull
    private int x;

    @DatabaseField(columnName = "chunk_z", canBeNull = false)
    @NotNull
    private int z;

    @DatabaseField(columnName = "blocks", canBeNull = false)
    @NotNull
    private String blocks;

    public ChunkLayer getBlocks() {
        return IridiumChunkBusters.getInstance().getPersist().load(ChunkLayer.class, new String(Base64.getDecoder().decode(blocks)));
    }

    public Chunk getChunk() {
        return Bukkit.getWorld(world).getChunkAt(x, z);
    }

    public BlockData(@NotNull ChunkBuster chunkBuster, @NotNull String world, int x, int y, int z, ChunkLayer blocks) {
        this.chunkBuster = chunkBuster;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blocks = new String(Base64.getEncoder().encode(IridiumChunkBusters.getInstance().getPersist().toString(blocks).getBytes()));
    }
}
