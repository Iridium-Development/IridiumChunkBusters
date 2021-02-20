package com.iridium.chunkbusters.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public abstract class Command {
    @NotNull
    private final List<String> aliases;
    @NotNull
    private final String description;
    @NotNull
    private final String permission;
    private final boolean player;
    private final boolean enabled = true;

    public Command(@NotNull List<String> aliases, @NotNull String description, @NotNull String permission, boolean player) {
        this.aliases = aliases;
        this.description = description;
        this.permission = permission;
        this.player = player;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args);
}
