package io.github.aboodz.summer.db;

import io.r2dbc.client.R2dbc;

import javax.inject.Inject;
import javax.inject.Provider;

public class DatabaseClient implements Provider<R2dbc> {

    private final R2dbc r2dbc;

    @Inject
    DatabaseClient(DatabaseConnection databaseConnection) {
    }

    @Override
    public R2dbc get() {
        new R2dbc(databaseConnection.getPool());    }
}
