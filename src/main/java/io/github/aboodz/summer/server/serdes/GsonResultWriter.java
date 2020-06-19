package io.github.aboodz.summer.server.serdes;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.lang.reflect.Type;

public class GsonResultWriter implements ResultWriter {

    private final Gson gson;

    @Inject
    GsonResultWriter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public WriterFunction write(Object obj) {
        return write(obj, obj.getClass());
    }

    @Override
    public WriterFunction write(Object obj, Type typeOfSrc) {
        return new WriterFunction() {
            @Override
            public MediaType getMediaType() {
                return MediaType.JSON_UTF_8;
            }

            @Override
            public Publisher<byte[]> get() {
                return Mono.fromSupplier(() -> gson.toJson(obj, typeOfSrc).getBytes());
            }
        };
    }

}
