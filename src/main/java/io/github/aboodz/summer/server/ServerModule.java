package io.github.aboodz.summer.server;


import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import io.github.aboodz.summer.server.serdes.GsonSerdesProvider;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Gson.class).toProvider(GsonSerdesProvider.class);
        bind(HandlerResolver.class).to(DefaultHandlerResolver.class);
    }

}
