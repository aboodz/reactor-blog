package io.github.aboodz.db;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseConnection {

    private final ConnectionPool pool;

    @Inject
    DatabaseConnection(DatabaseConfiguration databaseConfiguration) {
        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(databaseConfiguration.getHost())
                        .port(databaseConfiguration.getPort())
                        .database(databaseConfiguration.getDatabase())
                        .username(databaseConfiguration.getUsername())
                        .password(databaseConfiguration.getPassword())
                        .build()
        );

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(postgresqlConnectionFactory)
                .build();

        this.pool = new ConnectionPool(configuration);
    }

    public ConnectionPool getPool() {
        return pool;
    }
}
