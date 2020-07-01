package io.github.aboodz.server.serdes.gson;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import io.github.aboodz.server.serdes.ObjectWriter;
import io.github.aboodz.server.serdes.WriterFunction;
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
    public <T extends Serializable> WriterFunction write(T obj) {
        return write(Mono.just(obj), (Class<T>) obj.getClass());
    }

    @Override
    public <T extends Serializable> WriterFunction write(T obj, Class<T> typeOfSrc) {
        return write(Mono.just(obj), typeOfSrc);
    }

    @Override
    public <T extends Serializable> WriterFunction write(Publisher<T> obj, Class<T> typeOfSrc) {
        return new WriterFunction() {
            @Override
            public MediaType getMediaType() {
                return MediaType.JSON_UTF_8;
            }

            @Override
            public Publisher<byte[]> get() {
                return Mono.from(obj)
                        .map((o) -> gson.toJson(o, typeOfSrc).getBytes());
            }
        };
    }


}
