package io.github.aboodz.server;

import com.google.common.base.VerifyException;
import io.github.aboodz.server.exceptions.ErrorResponse;
import io.github.aboodz.server.exceptions.ManagedHandlerException;
import io.github.aboodz.server.serdes.ObjectWriter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.BiFunction;
import java.util.function.Function;

@Singleton
@Log4j2
public class DefaultHandlerResolver implements HandlerResolver {

    private final ObjectWriter objectWriter;

    @Inject
    DefaultHandlerResolver(ObjectWriter objectWriter) {
        this.objectWriter = objectWriter;
    }

    @Override
    public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> resolve(Function<HttpServerRequest, Mono<HandlerResult>> handler) {
        return (request, response) -> Mono.just(request)
                .flatMap(handler)
                .onErrorMap(VerifyException.class, e -> new ManagedHandlerException(HttpResponseStatus.BAD_REQUEST, e.getMessage()))
                .onErrorResume(ManagedHandlerException.class, e -> {
                    HandlerResult errorResult = HandlerResult.status(e.getStatus())
                            .body(objectWriter.write(e.toErrorResponse()));
                    return Mono.just(errorResult);
                })
                .onErrorResume(RuntimeException.class, e -> {
                    log.error("processing http request failed", e);
                    ErrorResponse errorResponse = ErrorResponse.technicalError(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    HandlerResult errorResult = HandlerResult.status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                            .body(objectWriter.write(errorResponse));
                    return Mono.just(errorResult);
                })
                .flatMapMany(handlerResult -> {
                    response.status(handlerResult.getStatus());

                    handlerResult.getHeaders()
                            .put(HttpHeaderNames.CONTENT_TYPE.toString(), handlerResult.getBodyWriter().getMediaType().toString());
                    handlerResult.getHeaders().asMap()
                            .forEach((header, values) -> values.forEach(headerValue -> response.addHeader(header, headerValue)));

                    return response.sendByteArray(handlerResult.getBodyWriter().body());
                });
    }

}
