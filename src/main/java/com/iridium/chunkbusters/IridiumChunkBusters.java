package com.iridium.chunkbusters;

import com.iridium.chunkbusters.commands.CommandManager;
import com.iridium.chunkbusters.configs.Configuration;
import com.iridium.chunkbusters.configs.Messages;
import com.iridium.chunkbusters.listeners.BlockPlaceListener;
import com.iridium.chunkbusters.listeners.InventoryClickListener;
import com.iridium.chunkbusters.nms.NMS;
import com.iridium.chunkbusters.support.Factions;
import com.iridium.chunkbusters.support.FactionsUUID;
import com.iridium.chunkbusters.support.FactionsX;
import com.iridium.chunkbusters.support.Support;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class IridiumChunkBusters extends JavaPlugin {

    private static IridiumChunkBusters instance;
    private Persist persist;

    private CommandManager commandManager;

    private Configuration configuration;
    private Messages messages;

    private final List<Support> supportedPlugins = new ArrayList<>();

    private NMS nms;

    @Override
    public void onEnable() {
        try {
            nms = (NMS) Class.forName("com.iridium.chunkbusters.nms." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]).newInstance();
        } catch (ClassNotFoundException e) {
            //Unsupported Version
            getLogger().info("Unsupported Version Detected: " + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
            getLogger().info("Try updating from spigot");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getDataFolder().mkdir();
        instance = this;
        this.persist = new Persist(Persist.PersistType.YAML);
        super.onEnable();
        this.commandManager = new CommandManager("chunkbusters");
        loadConfigs();
        saveConfigs();
        registerListeners();
        registerSupport();
        getLogger().info("----------------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");
        getLogger().info("----------------------------------------");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("-------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Disabled!");
        getLogger().info("");
        getLogger().info("-------------------------------");
    }

    public void loadConfigs() {
        this.configuration = persist.load(Configuration.class);
        this.messages = persist.load(Messages.class);
    }

    public void saveConfigs() {
        this.persist.save(configuration);
        this.persist.save(messages);
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    public void registerSupport() {
        if (Bukkit.getPluginManager().isPluginEnabled("FactionsX")) supportedPlugins.add(new FactionsX());
        if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            if (Bukkit.getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("drtshock")) {
                supportedPlugins.add(new FactionsUUID());
            } else {
                supportedPlugins.add(new Factions());
            }
        }
    }

    public static IridiumChunkBusters getInstance() {
        return instance;
    }
}
