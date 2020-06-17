package io.github.aboodz.summer.server;

import reactor.netty.http.server.HttpServerRoutes;

import java.util.function.Consumer;

public interface Routable {

    void defineRoutes(HttpServerRoutes routes);

}
