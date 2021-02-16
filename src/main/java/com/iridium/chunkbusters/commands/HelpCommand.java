package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.IridiumChunkBusters;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(Collections.singletonList("help"), "Displays the plugin commands", "", true);
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        int page = 1;
        if (args.length == 2) {
            if (!StringUtils.isNumeric(args[1])) {
                cs.sendMessage(com.iridium.chunkbusters.utils.StringUtils.color(IridiumChunkBusters.getInstance().getMessages().mustBeANumber.replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
                return;
            }
            page = Integer.parseInt(args[1]);
        }
        int maxpage = (int) Math.ceil(IridiumChunkBusters.getInstance().getCommandManager().commands.size() / 18.00);
        int current = 0;
        p.sendMessage(com.iridium.chunkbusters.utils.StringUtils.color(IridiumChunkBusters.getInstance().getMessages().helpHeader));
        for (Command command : IridiumChunkBusters.getInstance().getCommandManager().commands) {
            if ((p.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("") || command.getPermission().equalsIgnoreCase("iridiumskyblock.")) && command.isEnabled()) {
                if (current >= (page - 1) * 18 && current < page * 18)
                    p.sendMessage(com.iridium.chunkbusters.utils.StringUtils.color(IridiumChunkBusters.getInstance().getMessages().helpMessage.replace("%command%", command.getAliases().get(0)).replace("%description%", command.getDescription())));
                current++;
            }
        }
        BaseComponent[] components = TextComponent.fromLegacyText(com.iridium.chunkbusters.utils.StringUtils.color(IridiumChunkBusters.getInstance().getMessages().helpfooter.replace("%maxpage%", maxpage + "").replace("%page%", page + "")));

        for (BaseComponent component : components) {
            if (ChatColor.stripColor(component.toLegacyText()).contains(IridiumChunkBusters.getInstance().getMessages().nextPage)) {
                if (page < maxpage) {
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is help " + (page + 1)));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumChunkBusters.getInstance().getMessages().helpPageHoverMessage.replace("%page%", "" + (page + 1))).create()));
                }
            } else if (ChatColor.stripColor(component.toLegacyText()).contains(IridiumChunkBusters.getInstance().getMessages().previousPage)) {
                if (page > 1) {
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is help " + (page - 1)));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumChunkBusters.getInstance().getMessages().helpPageHoverMessage.replace("%page%", "" + (page - 1))).create()));
                }
            }
        }
        p.getPlayer().spigot().sendMessage(components);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
