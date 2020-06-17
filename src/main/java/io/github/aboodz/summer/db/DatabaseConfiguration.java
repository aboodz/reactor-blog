package io.github.aboodz.summer.db;

import lombok.Getter;

@Getter
public class DatabaseConfiguration {
    private final String host;
    private final Integer port;
    private final String database;
    private final String username;
    private final String password;


    public DatabaseConfiguration() {
        this.host = System.getProperty("database.connection.host");
        this.port = Integer.parseInt(System.getProperty("database.connection.port"));
        this.database = System.getProperty("database.connection.database");
        this.username = System.getProperty("database.connection.username");
        this.password = System.getProperty("database.connection.password");
    }

}
