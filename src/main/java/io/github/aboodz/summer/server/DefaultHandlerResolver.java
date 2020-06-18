package io.github.aboodz.summer.server;

import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.BiFunction;
import java.util.function.Function;

@Singleton
public class DefaultHandlerResolver implements HandlerResolver {

    private final Gson gson;

    @Inject
    public DefaultHandlerResolver(Gson gson) {
        this.gson = gson;
    }

    public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolve(Function<HttpServerRequest, ResponseBuilder> handlerFunction) {
        return (request, response) -> handlerFunction.apply(request).apply(response, gson);
    }

}
