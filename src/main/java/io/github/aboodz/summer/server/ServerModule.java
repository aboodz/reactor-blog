package io.github.aboodz.summer.server;


import com.google.inject.AbstractModule;
import io.github.aboodz.summer.server.serdes.SerDesModule;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new SerDesModule());
        bind(HandlerResolver.class).to(DefaultHandlerResolver.class);
    }

}
