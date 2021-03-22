package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(Collections.singletonList("help"), "Displays the plugin commands", "", false);
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        cs.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().helpHeader));
        for (Command command : IridiumChunkBusters.getInstance().getCommandManager().commands) {
            if ((cs.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("") || command.getPermission().equalsIgnoreCase("iridiumchunkbusters.")) && command.isEnabled()) {
                cs.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().helpMessage.replace("%command%", command.getAliases().get(0)).replace("%description%", command.getDescription())));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
