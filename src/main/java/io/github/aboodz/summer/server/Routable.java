package io.github.aboodz.summer.server;

import reactor.netty.http.server.HttpServerRoutes;

public interface Routable {

    void defineRoutes(HttpServerRoutes routes);

}
