package io.github.aboodz.summer.db;

import com.google.inject.AbstractModule;
import io.r2dbc.client.R2dbc;

public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(R2dbc.class).toProvider(R2dbcProvider.class).asEagerSingleton();
    }

}
