package io.github.aboodz.server;

import com.google.common.base.Stopwatch;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Set;

@Log4j2
@Singleton
public class HttpServer {

    private final reactor.netty.http.server.HttpServer reactorServer;

    @Inject
    HttpServer(ServerConfiguration serverConfiguration, HandlerResolver handlerResolver, Set<Routable> routable) {
        this.reactorServer = reactor.netty.http.server.HttpServer.create()
                .route(routes -> routable.forEach(r -> r.defineRoutes(routes, handlerResolver)))
                .port(serverConfiguration.getPort());
    }

    public void start() {
        log.debug("Starting web server");
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread thread = new Thread(() -> reactorServer.bindUntilJavaShutdown(Duration.ofMinutes(1L), disposableServer -> {
            stopwatch.stop();
            Duration elapsed = stopwatch.elapsed();
            log.info("Server started successfully in {} seconds. Listening on {}:{}", elapsed.getSeconds(),  disposableServer.host(), disposableServer.port());
        }));
        thread.setDaemon(false);
        thread.start();
    }

}
