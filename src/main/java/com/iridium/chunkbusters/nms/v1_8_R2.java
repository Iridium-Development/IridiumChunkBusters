package com.iridium.chunkbusters.nms;

import com.iridium.chunkbusters.IridiumChunkBusters;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class v1_8_R2 implements NMS {
    @Override
    public void setBlockFast(org.bukkit.World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
        net.minecraft.server.v1_8_R2.World nmsWorld = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R2.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        IBlockData ibd = net.minecraft.server.v1_8_R2.Block.getByCombinedId(blockId + (data << 12));

        ChunkSection cs = nmsChunk.getSections()[y >> 4];
        if (cs == null) {
            cs = new ChunkSection(y >> 4 << 4, true);
            nmsChunk.getSections()[y >> 4] = cs;
        }
        cs.setType(x & 15, y & 15, z & 15, ibd);
    }

    @Override
    public void sendChunk(Chunk chunk, List<Location> blocks, List<Player> players) {
        net.minecraft.server.v1_8_R2.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
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
            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), true, 65535);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\":\"" + message + "\"}"));
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
