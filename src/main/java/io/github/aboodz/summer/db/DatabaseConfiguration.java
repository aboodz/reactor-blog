package io.github.aboodz.summer.db;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Singleton
public class DatabaseConfiguration {

    private final String DB_CONFIGURATION_PATH = "/database.properties";
    private static final String DB_HOST_PROP = "database.connection.host";
    private static final String DB_PORT_PROP = "database.connection.port";
    private static final String DB_DATABASE_PROP = "database.connection.database";
    private static final String DB_USERNAME_PROP = "database.connection.username";
    private static final String DB_PASSWORD_PROP = "database.connection.password";

    private final String host;
    private final Integer port;
    private final String database;
    private final String username;
    private final String password;


    @SneakyThrows
    public DatabaseConfiguration() {
        try (InputStream resourceAsStream = DatabaseConfiguration.class.getResourceAsStream(DB_CONFIGURATION_PATH)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            this.host = properties.getProperty(DB_HOST_PROP);
            this.port = Integer.parseInt(properties.getProperty(DB_PORT_PROP));
            this.database = properties.getProperty(DB_DATABASE_PROP);
            this.username = properties.getProperty(DB_USERNAME_PROP);
            this.password = properties.getProperty(DB_PASSWORD_PROP);
        }
    }

}
