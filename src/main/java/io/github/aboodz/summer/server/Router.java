package io.github.aboodz.summer.server;

import lombok.SneakyThrows;
import reactor.netty.http.server.HttpServerRoutes;

import java.lang.reflect.Field;

public class Router {

    private final HttpServerRoutes routesBuilder;

    Router() {
        this.routesBuilder = HttpServerRoutes.newRoutes();
    }

    public HttpServerRoutes builder() {
        return routesBuilder;
    }

    @SneakyThrows
    void configure(HttpServerRoutes routes) {
        // This is really bad.. but I could not find an easy around this. creating an injectable consumer function is not possible
        // I am not going to create my own DSL route builder like Spring Router Function
        Field handlers = routes.getClass().getDeclaredField("handlers");
        handlers.setAccessible(true);
        handlers.set(routes, handlers.get(this.routesBuilder));
    }
}
