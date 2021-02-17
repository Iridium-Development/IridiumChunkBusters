package com.iridium.chunkbusters.nms;

import com.iridium.chunkbusters.IridiumChunkBusters;
import net.minecraft.server.v1_9_R1.ChunkSection;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.PacketPlayOutMultiBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class v1_9_R1 implements NMS {
    @Override
    public void setBlockFast(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
        net.minecraft.server.v1_9_R1.World nmsWorld = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_9_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        IBlockData ibd = net.minecraft.server.v1_9_R1.Block.getByCombinedId(blockId + (data << 12));

        ChunkSection cs = nmsChunk.getSections()[y >> 4];
        if (cs == null) {
            cs = new ChunkSection(y >> 4 << 4, true);
            nmsChunk.getSections()[y >> 4] = cs;
        }
        cs.setType(x & 15, y & 15, z & 15, ibd);
    }

    @Override
    public void sendChunk(Chunk chunk, List<Location> blocks, List<Player> players) {
        net.minecraft.server.v1_9_R1.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
        int blocksAmount = blocks.size();
        short[] values = new short[blocksAmount];

        Location firstLocation = null;

        int counter = 0;
        for (Location location : blocks) {
            if (firstLocation == null)
                firstLocation = location;

            values[counter++] = (short) ((location.getBlockX() & 15) << 12 | (location.getBlockZ() & 15) << 8 | location.getBlockY());
        }

        PacketPlayOutMultiBlockChange multiBlockChange = new PacketPlayOutMultiBlockChange(blocksAmount, values, nmsChunk);

        players.forEach(player -> {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(multiBlockChange);
        });
    }

    @Override
    public void sendChunk(Chunk chunk, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> chunk.getWorld().getPlayers().forEach(player -> {
            net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk packetPlayOutMapChunk = new net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk(((org.bukkit.craftbukkit.v1_15_R1.CraftChunk) chunk).getHandle(), 65535);
            ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }));
    }
}