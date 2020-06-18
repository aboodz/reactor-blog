package io.github.aboodz.summer.server;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.Function;

public interface ResponseBuilder extends Function<HttpServerResponse, Publisher<Void>>{

    static ResponseBuilder ok(String body) {
        return httpServerResponse -> httpServerResponse.status(HttpResponseStatus.OK)
                .sendString(Flux.just(body));
    }

}
