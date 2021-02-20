package com.iridium.chunkbusters.commands;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.utils.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    public List<Command> commands = new ArrayList<>();

    public CommandManager(String command) {
        IridiumChunkBusters.getInstance().getCommand(command).setExecutor(this);
        IridiumChunkBusters.getInstance().getCommand(command).setTabCompleter(this);
        registerCommands();
    }

    public void registerCommands() {
        registerCommand(new HelpCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new GiveCommand());
        registerCommand(new LogsCommand());
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    @Override
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length != 0) {
            for (Command command : commands) {
                if (command.getAliases().contains(args[0]) && command.isEnabled()) {
                    if (command.isPlayer() && !(cs instanceof Player)) {
                        // Must be a player
                        cs.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().mustBeAPlayer
                                .replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
                        return true;
                    }
                    if ((cs.hasPermission(command.getPermission()) || command.getPermission()
                            .equalsIgnoreCase("") || command.getPermission()
                            .equalsIgnoreCase("iridiumchunkbusters.")) && command.isEnabled()) {
                        command.execute(cs, args);
                    } else {
                        // No permission
                        cs.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().noPermission
                                .replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
                    }
                    return true;
                }
            }
        } else {
            if (cs instanceof Player) {
                Player p = (Player) cs;
                return true;
            }
        }
        cs.sendMessage(StringUtils.color(IridiumChunkBusters.getInstance().getMessages().unknownCommand
                .replace("%prefix%", IridiumChunkBusters.getInstance().getConfiguration().prefix)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (Command command : commands) {
                for (String alias : command.getAliases()) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && (
                            command.isEnabled() && (cs.hasPermission(command.getPermission())
                                    || command.getPermission().equalsIgnoreCase("") || command.getPermission()
                                    .equalsIgnoreCase("iridiumchunkbusters.")))) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }
        for (Command command : commands) {
            if (command.getAliases().contains(args[0]) && (command.isEnabled() && (
                    cs.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("")
                            || command.getPermission().equalsIgnoreCase("iridiumchunkbusters.")))) {
                return command.onTabComplete(cs, cmd, s, args);
            }
        }
        return null;
    }
}
