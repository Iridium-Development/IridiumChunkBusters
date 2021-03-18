package com.iridium.chunkbusters.nms;

import com.iridium.chunkbusters.IridiumChunkBusters;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.legacy.CraftLegacy;
import org.bukkit.entity.Player;

import java.util.List;

public class v1_15_R1 implements NMS {

    static {
        CraftLegacy.init();
    }

    @Override
    public void setBlockFast(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
        net.minecraft.server.v1_15_R1.World nmsWorld = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_15_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        IBlockData ibd = net.minecraft.server.v1_15_R1.Block.getByCombinedId(blockId + (data << 12));

        net.minecraft.server.v1_15_R1.ChunkSection cs = nmsChunk.getSections()[y >> 4];
        if (cs == null) {
            cs = new ChunkSection(y >> 4 << 4);
            nmsChunk.getSections()[y >> 4] = cs;
        }
        cs.setType(x & 15, y & 15, z & 15, ibd);
        nmsChunk.getWorld().getChunkProvider().getLightEngine().a(new BlockPosition(x, y, z));
    }

    @Override
    public void sendChunk(Chunk chunk, List<Location> blocks, List<Player> players) {
        net.minecraft.server.v1_15_R1.Chunk nmsChunk = ((org.bukkit.craftbukkit.v1_15_R1.CraftChunk) chunk).getHandle();
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
            EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(multiBlockChange);
        });
    }

    @Override
    public void sendChunk(Chunk chunk, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(IridiumChunkBusters.getInstance(), () -> chunk.getWorld().getPlayers().forEach(player -> {
            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\":\"" + message + "\"}"));
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, iChatBaseComponent, 0, 20, 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
    }
}
