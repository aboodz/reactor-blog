package io.github.aboodz.summer.test.db;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import io.github.aboodz.summer.db.DatabaseClient;
import io.github.aboodz.summer.db.DatabaseConnection;
import io.github.aboodz.summer.db.DatabaseModule;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;

public class DatabaseFixture {

    public DatabaseClient startAndSchemaDatabase() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.3-alpine")
                .withClasspathResourceMapping("./sql/1-init.sql", "/docker-entrypoint-initdb.d/1.sql", BindMode.READ_ONLY)
                .withClasspathResourceMapping("./sql/posts-data.sql", "/docker-entrypoint-initdb.d/2.sql", BindMode.READ_ONLY)
//                .withCommand("postgres -c log_statement=all -c log_destination=stderr")
                .withStartupTimeoutSeconds(5);
        PostgreSQLR2DBCDatabaseContainer postgreSQLR2DBCDatabaseContainer = new PostgreSQLR2DBCDatabaseContainer(container);
        postgreSQLR2DBCDatabaseContainer.start();

//        container.followOutput(outputFrame -> System.out.println(outputFrame.getUtf8String()));

        ConnectionFactoryOptions options = PostgreSQLR2DBCDatabaseContainer.getOptions(container);
        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactoryProvider().create(options);
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(postgresqlConnectionFactory)
                .build();

        ConnectionPool pool = new ConnectionPool(configuration);
        return new DatabaseClient(pool);
    }

}
