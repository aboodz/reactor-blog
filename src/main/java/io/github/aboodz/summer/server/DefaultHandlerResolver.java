package io.github.aboodz.summer.server;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import io.github.aboodz.summer.server.exceptions.ErrorResponse;
import io.github.aboodz.summer.server.exceptions.ManagedHandlerException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringWriter;
import java.util.function.BiFunction;
import java.util.function.Function;

@Singleton
@Log4j2
public class DefaultHandlerResolver implements HandlerResolver {

    private final Gson gson;

    @Inject
    DefaultHandlerResolver(Gson gson) {
        this.gson = gson;
    }

    @Override
    public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolve(Function<HttpServerRequest, HandlerFunction> handlerFunction) {
        return (request, response) -> handlerFunction.apply(request).apply(response);
    }

    @Override
    public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolveMono(Function<HttpServerRequest, Publisher<HandlerFunction>> handlerFunction) {
        return (request, response) -> Mono.fromSupplier(() -> handlerFunction.apply(request))
                .flatMapMany(Flux::from)
                .flatMap(f -> f.apply(response))
                .onErrorResume(ManagedHandlerException.class, e -> {
                    response.status(e.getStatus());
                    ErrorResponse errorResponse = e.toErrorResponse();
                    return response.sendString(Mono.just(gson.toJson(errorResponse)));
                });
    }

}
