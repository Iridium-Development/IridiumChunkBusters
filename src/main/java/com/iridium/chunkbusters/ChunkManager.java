package com.iridium.chunkbusters;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.support.Support;
import com.iridium.chunkbusters.utils.ItemStackUtils;
import com.iridium.chunkbusters.utils.Placeholder;
import com.iridium.chunkbusters.utils.StringUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ChunkManager {

    public static ItemStack getChunkBuster(int size) {
        ItemStack itemStack = ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().chunkBuster, Collections.singletonList(new Placeholder("size", String.valueOf(size * 2 - 1))));
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("IridiumChunkBuster", size);
        return nbtItem.getItem();
    }

    public static int getChunkBusterSize(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey("IridiumChunkBuster") ? nbtItem.getInteger("IridiumChunkBuster") : 0;
    }

    public static void deleteChunks(Player player, Chunk c, int y, int radius) {
        radius--;
        int cx = c.getX();
        int cz = c.getZ();
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                Chunk chunk = c.getWorld().getChunkAt(x, z);
                deleteChunk(chunk, player, y);
            }
        }
    }

    public static void deleteChunk(final Chunk c, Player player, final int y) {
        if (y == 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.color("")));
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumChunkBusters.getInstance(), () -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().actionBarMessage.replace("{ylevel}", String.valueOf(y)))));
            int cx = c.getX() << 4;
            int cz = c.getZ() << 4;

            for (int x = cx; x < cx + 16; x++) {
                for (int z = cz; z < cz + 16; z++) {
                    Block block = c.getWorld().getBlockAt(x, y, z);
                    if (!IridiumChunkBusters.getInstance().getConfiguration().blacklist.contains(XMaterial.matchXMaterial(block.getType()))) {
                        boolean allowed = true;
                        for (Support support : IridiumChunkBusters.getInstance().getSupportedPlugins()) {
                            if (!support.canDelete(player, block)) allowed = false;
                        }
                        if (allowed) {
                            IridiumChunkBusters.getInstance().getNms().setBlockFast(c.getWorld(), x, y, z, 0, (byte) 0, false);
                        }
                    }
                }
            }
            IridiumChunkBusters.getInstance().getNms().sendChunk(c);
            deleteChunk(c, player, y - 1);
        }, IridiumChunkBusters.getInstance().getConfiguration().deleteInteval);
    }

}
