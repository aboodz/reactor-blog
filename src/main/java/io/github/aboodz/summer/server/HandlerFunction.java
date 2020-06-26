package io.github.aboodz.summer.server;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;
import io.github.aboodz.summer.server.serdes.WriterFunction;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.Function;

public class HandlerFunction implements Function<HttpServerResponse, Publisher<Void>> {

    protected HttpResponseStatus status;
    protected Multimap<String, String> headers = LinkedHashMultimap.create();
    protected WriterFunction writerFunction = new WriterFunction() {
        @Override
        public MediaType getMediaType() {
            return MediaType.JSON_UTF_8;
        }

        @Override
        public Publisher<byte[]> get() {
            return Mono.just(new byte[0]);
        }
    };

    HandlerFunction(HttpResponseStatus status) {
        this.status = status;
    }

    public static HandlerFunction ok() {
        return status(HttpResponseStatus.OK);
    }

    public static HandlerFunction notFound() {
        return status(HttpResponseStatus.NOT_FOUND);
    }

    public static HandlerFunction status(HttpResponseStatus status) {
        return new HandlerFunction(status);
    }

    public HandlerFunction header(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public HandlerFunction body(WriterFunction writerFunction) {
        this.writerFunction = writerFunction;
        return this;
    }

    @Override
    public Publisher<Void> apply(HttpServerResponse response) {
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), writerFunction.getMediaType().toString());
        headers.asMap().forEach((header, values) -> values.forEach(headerValue -> response.addHeader(header, headerValue)));
        response.status(status);

        return response.sendByteArray(writerFunction.get());
    }

}