package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super(Collections.singletonList("reload"), "Reload your configurations", "iridiumchunkbusters.reload", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        IridiumChunkBusters.getInstance().loadConfigs();
        sender.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().reloaded.replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
