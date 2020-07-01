package io.github.aboodz.server;


import com.google.inject.AbstractModule;
import io.github.aboodz.server.serdes.gson.GsonModule;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GsonModule());
        bind(HandlerResolver.class).to(DefaultHandlerResolver.class);
    }

}
