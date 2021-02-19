package com.iridium.chunkbusters;

import com.heretere.hdl.dependency.maven.annotation.MavenDependency;
import com.heretere.hdl.relocation.annotation.Relocation;
import com.heretere.hdl.spigot.DependencyPlugin;
import com.iridium.chunkbusters.commands.CommandManager;
import com.iridium.chunkbusters.configs.Configuration;
import com.iridium.chunkbusters.configs.Messages;
import com.iridium.chunkbusters.configs.SQL;
import com.iridium.chunkbusters.database.ChunkBuster;
import com.iridium.chunkbusters.database.DatabaseManager;
import com.iridium.chunkbusters.listeners.BlockPlaceListener;
import com.iridium.chunkbusters.listeners.InventoryClickListener;
import com.iridium.chunkbusters.nms.NMS;
import com.iridium.chunkbusters.support.*;
import com.iridium.chunkbusters.utils.ItemStackUtils;
import com.iridium.chunkbusters.utils.Placeholder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MavenDependency("com|fasterxml|jackson|core:jackson-databind:2.12.1")
@MavenDependency("com|fasterxml|jackson|core:jackson-core:2.12.1")
@MavenDependency("com|fasterxml|jackson|core:jackson-annotations:2.12.1")
@MavenDependency("com|fasterxml|jackson|dataformat:jackson-dataformat-yaml:2.12.1")
@MavenDependency("org|yaml:snakeyaml:1.27")
@Relocation(from = "org|yaml", to = "com|iridium|chunkbusters")
@Getter
public class IridiumChunkBusters extends DependencyPlugin {

    private static IridiumChunkBusters instance;
    private Persist persist;
    private DatabaseManager databaseManager;

    private CommandManager commandManager;

    private Configuration configuration;
    private Messages messages;
    private SQL sql;

    private Support support;

    private NMS nms;

    private final List<ChunkBuster> activeChunkBusters = new ArrayList<>();

    @Override
    protected void enable() {
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
        this.commandManager = new CommandManager("chunkbusters");
        loadConfigs();
        saveConfigs();
        try {
            this.databaseManager = new DatabaseManager();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        databaseManager.getChunkBusters().thenAccept(chunkBusters -> chunkBusters.stream().filter(chunkBuster -> chunkBuster.getY() != 0).forEach(ChunkBuster::deleteChunks));
        registerListeners();
        this.support = getSupport();
        getLogger().info("----------------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");
        getLogger().info("----------------------------------------");
    }

    @Override
    protected void disable() {
        activeChunkBusters.forEach(chunkBuster -> databaseManager.saveChunkBuster(chunkBuster));
        IridiumChunkBusters.getInstance().getDatabaseManager().commitBlockData();
        getLogger().info("-------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Disabled!");
        getLogger().info("");
        getLogger().info("-------------------------------");
    }

    @Override
    public void load() {
    }

    public void loadConfigs() {
        this.configuration = persist.load(Configuration.class);
        this.messages = persist.load(Messages.class);
        this.sql = persist.load(SQL.class);
    }

    public void saveConfigs() {
        this.persist.save(configuration);
        this.persist.save(messages);
        this.persist.save(sql);
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    public Support getSupport() {
        if (Bukkit.getPluginManager().isPluginEnabled("FactionsX")) return new FactionsX();
        if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            if (Bukkit.getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("drtshock")) {
                return new FactionsUUID();
            } else {
                return new Factions();
            }
        }
        return new Default();
    }

    public ItemStack getChunkBuster(int size) {
        ItemStack itemStack = ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().chunkBuster, Collections.singletonList(new Placeholder("size", String.valueOf(size * 2 - 1))));
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("IridiumChunkBuster", size);
        return nbtItem.getItem();
    }

    public int getChunkBusterSize(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey("IridiumChunkBuster") ? nbtItem.getInteger("IridiumChunkBuster") : 0;
    }

    public static IridiumChunkBusters getInstance() {
        return instance;
    }
}
