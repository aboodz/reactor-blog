package io.github.aboodz.summer.server;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.http.server.HttpServerResponse;

import java.io.Serializable;
import java.util.function.BiFunction;

public class ResponseBuilder implements BiFunction<HttpServerResponse, Gson, Publisher<Void>> {

    protected HttpResponseStatus status;
    protected Multimap<String, String> headers = LinkedHashMultimap.create();
    protected Serializable body;

    ResponseBuilder(HttpResponseStatus status) {
        this.status = status;
    }

    public static ResponseBuilder ok() {
        return status(HttpResponseStatus.OK);
    }

    public static ResponseBuilder status(HttpResponseStatus status) {
        return new ResponseBuilder(status);
    }

    public ResponseBuilder header(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public ResponseBuilder body(Serializable body) {
        this.body = body;
        return this;
    }

    @SneakyThrows
    @Override
    public Publisher<Void> apply(HttpServerResponse response, Gson gson) {
        headers.asMap().forEach((header, values) -> values.forEach(headerValue -> response.addHeader(header, headerValue)));
        response.addHeader(HttpHeaderNames.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        response.sendHeaders();

        // at this stage, I am not sure if json should be here. also I am not happy of serializing into string. This loads
        // the object into memory before of writing it directly to the stream.
        Flux<String> jsonPublisher = Flux.just(gson.toJson(body));
        return response.sendString(jsonPublisher);
    }

}
