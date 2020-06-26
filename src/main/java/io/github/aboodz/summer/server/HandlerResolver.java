package io.github.aboodz.summer.server;

import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface HandlerResolver {

    BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolve(Function<HttpServerRequest, HandlerFunction> handlerFunction);
    BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolveMono(Function<HttpServerRequest, Publisher<HandlerFunction>> handlerFunction);
}
