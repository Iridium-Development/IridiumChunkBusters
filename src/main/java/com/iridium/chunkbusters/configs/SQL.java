package com.iridium.chunkbusters.configs;

public class SQL {

    public Driver driver = Driver.SQLITE;
    public String host = "localhost";
    public String database = "IridiumChunkBusters";
    public String username = "";
    public String password = "";
    public int port = 3306;

    public enum Driver {

        MYSQL,
        MARIADB,
        SQLSERVER,
        POSTGRESQL,
        H2,
        SQLITE
    }
}
