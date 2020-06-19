package io.github.aboodz.summer.server;

import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import javax.inject.Singleton;
import java.util.function.BiFunction;
import java.util.function.Function;

@Singleton
public class DefaultHandlerResolver implements HandlerResolver {

    public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolve(Function<HttpServerRequest, HandlerResult> handlerFunction) {
        return (request, response) -> handlerFunction.apply(request).apply(response);
    }

}
