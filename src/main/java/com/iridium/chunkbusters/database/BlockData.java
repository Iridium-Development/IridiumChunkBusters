package com.iridium.chunkbusters.database;

import com.iridium.chunkbusters.ChunkLayer;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
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
        return alternateDeserializer(blocks);
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
        this.blocks = new String(Base64.getEncoder().encode(alternateSerialize(blocks).getBytes()));
    }
    private ChunkLayer alternateDeserializer(String base64)
    {
        String ymlString = new String(Base64.getDecoder().decode(base64));
        //System.out.println(ymlString);
        ChunkLayer cl = new ChunkLayer();
        //ChunkLayer cl = IridiumChunkBusters.getInstance().getPersist().load(ChunkLayer.class, );
        String[] lines = ymlString.split("\n");
        boolean isReadingBlocks = false,isReadingData=false;
        int index1=0,index2=0;

        for (String line : lines) {
            if(line.contains("blocks:"))
            {
                isReadingBlocks=true;
                index1=-1;
                index2=0;
                continue;
            }
            if(line.contains("data:"))
            {
                isReadingBlocks=false;
                isReadingData=true;
                index1=0;
                continue;
            }
            if(isReadingBlocks)
            {
                if(line.contains("- - "))
                {
                    index1++;
                    index2=0;
                }
                //System.out.println("Line {"+line+"} Reading, i1="+index1+",i2="+index2);
                String substringed = line.substring(4);
                Material matched = Material.getMaterial(substringed);
                //System.out.println("Line {"+substringed+"} matched as "+matched);

                cl.blocks[index1][index2++]= matched;
            }
            if(isReadingData)
            {
                if(line.startsWith("  "))
                    cl.data[index1++] = Base64.getDecoder().decode(line.substring(2));
            }
        }

        return cl;
    }
    private String alternateSerialize(ChunkLayer cl)
    {
        //return IridiumChunkBusters.getInstance().getPersist().toString(cl);
        StringBuilder sb = new StringBuilder("---\n");
        sb.append("blocks:\n");

        for(Material[] row:cl.blocks)
        {
            boolean first=true;
            
            for(Material mat:row)
            {
                sb.append(first?"- ":"  ");
                sb.append("- ");
                if(mat!=null)
                    sb.append(mat.name());
                else
                    sb.append("null");
                sb.append("\n");
                first=false;
            }
        }

        sb.append("data:\n");
        for(byte[] row:cl.data)
        {
            sb.append("- !!binary |-\n");
            sb.append("  ");
            sb.append(new String(Base64.getEncoder().encode(row)));
            sb.append("\n");
        }

        return sb.toString();
    }
}