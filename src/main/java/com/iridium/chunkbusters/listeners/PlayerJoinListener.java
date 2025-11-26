package com.iridium.chunkbusters.listeners;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.iridiumcore.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && IridiumChunkBusters.getInstance().getConfiguration().patreonMessage) {
            Bukkit.getScheduler().runTaskLater(IridiumChunkBusters.getInstance(), () ->
                            player.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().prefix
                                    + " &7Thanks for using " + IridiumChunkBusters.getInstance().getDescription().getName()
                                    + ", if you like the plugin, consider donating at "
                                    + "&6&nwww.patreon.com/Peaches_MLG")), 5);
        }
    }
}