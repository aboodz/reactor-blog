package io.github.aboodz.summer.db;

import io.r2dbc.client.R2dbc;

import javax.inject.Inject;
import javax.inject.Provider;

public class R2dbcProvider implements Provider<R2dbc> {

    private final DatabaseConnection databaseConnection;

    @Inject
    R2dbcProvider(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public R2dbc get() {
        return new R2dbc(databaseConnection.getPool());
    }
}
