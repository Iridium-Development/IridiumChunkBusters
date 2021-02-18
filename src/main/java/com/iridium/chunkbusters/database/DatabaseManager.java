package com.iridium.chunkbusters.database;

import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.configs.SQL;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static final SQL SQL_CONFIG = IridiumChunkBusters.getInstance().getSql();

    private final Dao<ChunkBuster, Integer> chunkBustersDao;
    private final Dao<BlockData, Integer> blockDataDao;

    private final ConnectionSource connectionSource;

    public DatabaseManager() throws SQLException {
        String databaseURL = getDatabaseURL();

        connectionSource = new JdbcConnectionSource(
                databaseURL,
                SQL_CONFIG.username,
                SQL_CONFIG.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, ChunkBuster.class);
        TableUtils.createTableIfNotExists(connectionSource, BlockData.class);

        chunkBustersDao = DaoManager.createDao(connectionSource, ChunkBuster.class);
        blockDataDao = DaoManager.createDao(connectionSource, BlockData.class);

        blockDataDao.setAutoCommit(connectionSource.getReadWriteConnection(null), false);
    }

    private @NotNull String getDatabaseURL() {
        switch (SQL_CONFIG.driver) {
            case MYSQL:
            case MARIADB:
            case POSTGRESQL:
                return "jdbc:" + SQL_CONFIG.driver + "://" + SQL_CONFIG.host + ":" + SQL_CONFIG.port + "/" + SQL_CONFIG.database;
            case SQLSERVER:
                return "jdbc:sqlserver://" + SQL_CONFIG.host + ":" + SQL_CONFIG.port + ";databaseName=" + SQL_CONFIG.database;
            case H2:
                return "jdbc:h2:file:" + SQL_CONFIG.database;
            case SQLITE:
                return "jdbc:sqlite:" + new File(IridiumChunkBusters.getInstance().getDataFolder(), SQL_CONFIG.database + ".db");
        }

        throw new RuntimeException("How did we get here?");
    }

    public CompletableFuture<List<ChunkBuster>> getChunkBusters() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return chunkBustersDao.queryBuilder().query().stream().sorted(Comparator.comparing(ChunkBuster::getTime).reversed()).collect(Collectors.toList());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return new ArrayList<>();
        });
    }

    public void saveChunkBuster(@NotNull ChunkBuster chunkBuster) {
        try {
            chunkBustersDao.createOrUpdate(chunkBuster);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void saveBlockData(@NotNull BlockData blockData) {
        if(!IridiumChunkBusters.getInstance().getConfiguration().restoreChunkBusters)return;
        try {
            blockDataDao.createOrUpdate(blockData);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void commitBlockData() {
        if(!IridiumChunkBusters.getInstance().getConfiguration().restoreChunkBusters)return;
        try {
            blockDataDao.commit(connectionSource.getReadWriteConnection(null));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteChunkBuster(@NotNull ChunkBuster chunkBuster) {
        try {
            chunkBustersDao.delete(chunkBuster);
            for (BlockData blockData : chunkBuster.getBlockDataList()) {
                deleteBlockData(blockData);
            }
            commitBlockData();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteBlockData(@NotNull BlockData blockData) {
        try {
            blockDataDao.delete(blockData);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
