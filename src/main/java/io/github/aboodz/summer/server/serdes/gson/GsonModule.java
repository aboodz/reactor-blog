package io.github.aboodz.summer.server.serdes.gson;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import io.github.aboodz.summer.server.serdes.ObjectReader;
import io.github.aboodz.summer.server.serdes.ObjectWriter;

public class GsonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Gson.class).toProvider(GsonSerdesProvider.class);
        bind(ObjectWriter.class).to(GsonObjectWriter.class);
        bind(ObjectReader.class).to(GsonObjectReader.class);
    }

}
