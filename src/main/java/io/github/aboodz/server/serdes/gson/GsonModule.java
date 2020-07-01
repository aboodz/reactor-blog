package io.github.aboodz.server.serdes.gson;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import io.github.aboodz.server.serdes.ObjectReader;
import io.github.aboodz.server.serdes.ObjectWriter;

public class GsonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Gson.class).toProvider(GsonSerdesProvider.class);
        bind(ObjectWriter.class).to(GsonObjectWriter.class);
        bind(ObjectReader.class).to(GsonObjectReader.class);
    }

}
