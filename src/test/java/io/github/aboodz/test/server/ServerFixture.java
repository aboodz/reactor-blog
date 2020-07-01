package io.github.aboodz.test.server;

import io.github.aboodz.server.HandlerResolver;
import io.github.aboodz.server.Routable;

import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;


public class ServerFixture {

    public DisposableServer createTestingServer(Routable routable, HandlerResolver resolver) {
        return HttpServer.create()
                .port(0)
                .route(r -> routable.defineRoutes(r, resolver))
                .wiretap(true)
                .bindNow();
    }

    public HttpClient createTestingClient(DisposableServer server) {
        return HttpClient.create()
                .port(server.port())
                .wiretap(true);
    }

}
