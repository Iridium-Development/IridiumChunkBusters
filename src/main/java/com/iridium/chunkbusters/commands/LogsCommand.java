package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.gui.LogsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class LogsCommand extends Command {

    public LogsCommand() {
        super(Arrays.asList("logs"), "Show recent chunkbusters used", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.openInventory(new LogsGUI(player.getUniqueId(), 1).getInventory());
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
