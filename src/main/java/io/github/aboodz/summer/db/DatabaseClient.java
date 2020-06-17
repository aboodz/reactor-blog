package io.github.aboodz.summer.db;

import io.r2dbc.client.R2dbc;

public class DatabaseClient {

    private final R2dbc r2dbc;

    DatabaseClient(DatabaseConnection databaseConnection) {
        this.r2dbc = new R2dbc(databaseConnection.getPool());
    }
}
