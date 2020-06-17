package io.github.aboodz.summer.db;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DatabaseModule extends AbstractModule {

    @Provides
    public DatabaseConfiguration databaseConfiguration() {
        return new DatabaseConfiguration();
    }

    @Provides
    public DatabaseConnection databaseConnection(DatabaseConfiguration databaseConfiguration) {
        return new DatabaseConnection(databaseConfiguration);
    }

    @Provides
    public DatabaseClient databaseClient(DatabaseConnection databaseConnection) {
        return new DatabaseClient(databaseConnection);
    }

}
