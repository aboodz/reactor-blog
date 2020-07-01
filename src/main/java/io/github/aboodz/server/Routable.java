package io.github.aboodz.server;

import reactor.netty.http.server.HttpServerRoutes;

@FunctionalInterface
public interface Routable {

    void defineRoutes(HttpServerRoutes routes, HandlerResolver resolver);

}
