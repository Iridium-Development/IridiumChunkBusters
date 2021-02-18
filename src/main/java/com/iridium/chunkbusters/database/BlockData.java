package com.iridium.chunkbusters.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

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

    @DatabaseField(columnName = "x", canBeNull = false)
    @NotNull
    private int x;

    @DatabaseField(columnName = "y", canBeNull = false)
    @NotNull
    private int y;

    @DatabaseField(columnName = "z", canBeNull = false)
    @NotNull
    private int z;

    @DatabaseField(columnName = "material", canBeNull = false)
    @NotNull
    private Material material;

    @DatabaseField(columnName = "data", canBeNull = false)
    @NotNull
    private Byte data;

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public BlockData(ChunkBuster chunkBuster, BlockState blockState) {
        this.chunkBuster = chunkBuster;
        this.world = blockState.getWorld().getName();
        this.x = blockState.getX();
        this.y = blockState.getY();
        this.z = blockState.getZ();
        this.material = blockState.getType();
        this.data = blockState.getRawData();
    }
}
