package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.gui.LogsGUI;
import com.iridium.chunkbusters.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LogsCommand extends Command {

    public LogsCommand() {
        super(Collections.singletonList("logs"), "Show recent chunkbusters used", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            player.openInventory(new LogsGUI(player.getUniqueId(), 1).getInventory());
        } else if (player.hasPermission("iridiumchunkbusters.viewotherlogs")) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (offlinePlayer.hasPlayedBefore()) {
                player.openInventory(new LogsGUI(offlinePlayer.getUniqueId(), 1).getInventory());
            } else {
                sender.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().unknownPlayer.replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
