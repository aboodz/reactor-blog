package io.github.aboodz.summer.server;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface HandlerResolver {

    BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolve(Function<HttpServerRequest, HandlerResult> handlerFunction);
    BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolveMono(Function<HttpServerRequest, Mono<HandlerResult>> handlerFunction);
}
