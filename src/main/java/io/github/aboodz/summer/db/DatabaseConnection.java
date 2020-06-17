package io.github.aboodz.summer.db;


import io.r2dbc.client.R2dbc;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;

import javax.inject.Singleton;

@Singleton
public class DatabaseConnection {

    private final ConnectionPool pool;

    DatabaseConnection(DatabaseConfiguration databaseConfiguration) {
        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .port(55442)
                        .database("blog")
                        .username("blog-db-user")
                        .password("strong-password")
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
