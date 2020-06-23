package io.github.aboodz.summer.test.db;

import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;

public class DatabaseFixture {

    public R2dbc startAndSchemaDatabase() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.3-alpine")
                .withClasspathResourceMapping("./sql/1-init.sql", "/docker-entrypoint-initdb.d/1.sql", BindMode.READ_ONLY)
                .withClasspathResourceMapping("./sql/posts-data.sql", "/docker-entrypoint-initdb.d/2.sql", BindMode.READ_ONLY)
                .withStartupTimeoutSeconds(5);
        PostgreSQLR2DBCDatabaseContainer postgreSQLR2DBCDatabaseContainer = new PostgreSQLR2DBCDatabaseContainer(container);
        postgreSQLR2DBCDatabaseContainer.start();

        ConnectionFactoryOptions options = PostgreSQLR2DBCDatabaseContainer.getOptions(container);
        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactoryProvider().create(options);
        return new R2dbc(postgresqlConnectionFactory);
    }

}
