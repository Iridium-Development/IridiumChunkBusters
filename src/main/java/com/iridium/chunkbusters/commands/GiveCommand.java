package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.IridiumChunkBusters;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GiveCommand extends Command {

    public GiveCommand() {
        super(Arrays.asList("give", "givechunkbuster"), "Gives a player a chunkbuster", "iridiumchunkbusters.give", false);
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if (args.length != 3) {
            cs.sendMessage("/chunkbuster give <player> <radius>");
            return;
        }

        if (!StringUtils.isNumeric(args[2])) {
            cs.sendMessage(com.iridium.chunkbusters.utils.StringUtils.color(IridiumChunkBusters.getInstance().getMessages().mustBeANumber.replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
            return;
        }
        Player player = Bukkit.getPlayer(args[1]);
        if (player != null) {
            int amount = Integer.parseInt(args[2]);
            player.getInventory().addItem(IridiumChunkBusters.getInstance().getChunkBuster(amount));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
