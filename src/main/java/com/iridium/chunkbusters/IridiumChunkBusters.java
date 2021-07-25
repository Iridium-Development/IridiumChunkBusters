package com.iridium.chunkbusters;


import com.iridium.chunkbusters.commands.CommandManager;
import com.iridium.chunkbusters.configs.Configuration;
import com.iridium.chunkbusters.configs.Messages;
import com.iridium.chunkbusters.configs.SQL;
import com.iridium.chunkbusters.database.ChunkBuster;
import com.iridium.chunkbusters.database.DatabaseManager;
import com.iridium.chunkbusters.gui.ConfirmationGUI;
import com.iridium.chunkbusters.listeners.BlockPlaceListener;
import com.iridium.chunkbusters.listeners.InventoryClickListener;
import com.iridium.chunkbusters.listeners.PlayerInteractListener;
import com.iridium.chunkbusters.support.*;
import com.iridium.chunkbusters.utils.ItemStackUtils;
import com.iridium.iridiumcore.IridiumCore;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class IridiumChunkBusters extends IridiumCore {

    private static IridiumChunkBusters instance;
    private DatabaseManager databaseManager;

    private CommandManager commandManager;

    private Configuration configuration;
    private Messages messages;
    private SQL sql;

    private Support support;


    private final List<ChunkBuster> activeChunkBusters = new ArrayList<>();

    private final List<ConfirmationGUI> confirmationGUIS = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        getDataFolder().mkdir();
        instance = this;
        this.commandManager = new CommandManager("chunkbusters");
        try {
            this.databaseManager = new DatabaseManager();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this.support = getSupport();
        databaseManager.getChunkBusters().thenAccept(chunkBusters -> chunkBusters.stream().filter(chunkBuster -> chunkBuster.getY() != 0).forEach(ChunkBuster::deleteChunks));
        new Metrics(this, 9403);
        getLogger().info("----------------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");
        getLogger().info("----------------------------------------");
    }

    @Override
    public void loadConfigs() {
        this.configuration = getPersist().load(Configuration.class);
        this.messages = getPersist().load(Messages.class);
        this.sql = getPersist().load(SQL.class);
    }

    @Override
    public void saveConfigs() {
        getPersist().save(configuration);
        getPersist().save(messages);
        getPersist().save(sql);
    }

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
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

    @Override
    public void saveData() {
        activeChunkBusters.forEach(chunkBuster -> databaseManager.saveChunkBuster(chunkBuster));
    }

    public static IridiumChunkBusters getInstance() {
        return instance;
    }
}
