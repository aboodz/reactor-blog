package io.github.aboodz.summer.server;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.Function;
import java.util.function.Supplier;

public class HandlerResult implements Function<HttpServerResponse, Publisher<Void>> {

    protected HttpResponseStatus status;
    protected Multimap<String, String> headers = LinkedHashMultimap.create();
    protected String body;

    HandlerResult(HttpResponseStatus status) {
        this.status = status;
    }

    public static HandlerResult ok() {
        return status(HttpResponseStatus.OK);
    }

    public static HandlerResult status(HttpResponseStatus status) {
        return new HandlerResult(status);
    }

    public HandlerResult header(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public HandlerResult body(Supplier<String> writerFunction) {
        this.body = writerFunction.get();
        return this;
    }

    @SneakyThrows
    @Override
    public Publisher<Void> apply(HttpServerResponse response) {
        headers.asMap().forEach((header, values) -> values.forEach(headerValue -> response.addHeader(header, headerValue)));

        // TODO: move adding headers to writer function
        response.addHeader(HttpHeaderNames.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        response.sendHeaders();

        // at this stage, I am not sure if json should be here. also I am not happy of serializing into string. This loads
        // the object into memory before of writing it directly to the stream.
        Flux<String> jsonPublisher = Flux.just(body);
        return response.sendString(jsonPublisher);
    }

}
