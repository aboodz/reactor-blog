package io.github.aboodz.summer.server.serdes;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;

public class SerDesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Gson.class).toProvider(GsonSerdesProvider.class);
        bind(ObjectReader.class).to(GsonObjectReader.class);
    }
}
