package io.github.aboodz.server;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.net.URI;

@Getter
public class HandlerResult {

    public interface BodyWriter {
        MediaType getMediaType();
        Publisher<byte[]> body();
    }

    private HttpResponseStatus status;
    private Multimap<String, String> headers = LinkedHashMultimap.create();
    private BodyWriter bodyWriter = new BodyWriter() {
        @Override
        public MediaType getMediaType() {
            return MediaType.ANY_TYPE;
        }

        @Override
        public Publisher<byte[]> body() {
            return Mono.empty();
        }
    };

    HandlerResult(HttpResponseStatus status) {
        this.status = status;
    }

    public static HandlerResult ok() {
        return status(HttpResponseStatus.OK);
    }

    @SneakyThrows
    public static HandlerResult created(String location) {
        return created(new URI(location));
    }

    public static HandlerResult created(URI location) {
        HandlerResult handlerResult = status(HttpResponseStatus.CREATED);
        handlerResult.headers.put(HttpHeaderNames.LOCATION.toString(), location.toString());
        return handlerResult;
    }

    public static HandlerResult noContent() {
        return status(HttpResponseStatus.NO_CONTENT);
    }

    public static HandlerResult notFound() {
        return status(HttpResponseStatus.NOT_FOUND);
    }

    public static HandlerResult status(HttpResponseStatus status) {
        return new HandlerResult(status);
    }

    public HandlerResult header(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public HandlerResult body(BodyWriter bodyWriter) {
        this.bodyWriter = bodyWriter;
        return this;
    }

}
