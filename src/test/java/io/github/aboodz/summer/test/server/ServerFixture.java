package io.github.aboodz.summer.test.server;

import io.github.aboodz.summer.server.Routable;

import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;


public class ServerFixture {

    public DisposableServer createTestingServer(Routable routable) {
        return HttpServer.create()
                .port(0)
                .route(routable::defineRoutes)
                .wiretap(true)
                .bindNow();
    }

    public HttpClient createTestingClient(DisposableServer server) {
        return HttpClient.create()
                .port(server.port())
                .wiretap(true);
    }

}
