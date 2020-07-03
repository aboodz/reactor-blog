package io.github.aboodz.server.serdes.gson;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import io.github.aboodz.server.HandlerResult;
import io.github.aboodz.server.serdes.ObjectWriter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.io.Serializable;

public class GsonObjectWriter implements ObjectWriter {

    private final Gson gson;

    @Inject
    GsonObjectWriter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T extends Serializable> HandlerResult.BodyWriter write(T obj) {
        return write(obj, (Class<T>) obj.getClass());
    }

    @Override
    public <T extends Serializable> HandlerResult.BodyWriter write(T obj, Class<T> typeOfSrc) {
        return new HandlerResult.BodyWriter() {
            @Override
            public MediaType getMediaType() {
                return MediaType.JSON_UTF_8;
            }

            @Override
            public Publisher<byte[]> body() {
                return Mono.just(obj)
                        .map(o -> gson.toJson(o, typeOfSrc).getBytes());
            }
        };
    }

}
