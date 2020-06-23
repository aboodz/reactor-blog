package io.github.aboodz.summer.server.serdes;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Type;

public class GsonResultWriter implements ResultWriter {

    private final Gson gson;

    @Inject
    GsonResultWriter(Gson gson) {
        this.gson = gson;
    }

//    @Override
//    public WriterFunction write(Object obj) {
//        return write(obj, obj.getClass());
//    }
//
//    @Override
//    public WriterFunction write(Object obj, Type typeOfSrc) {
//        return write(Mono.just(obj), typeOfSrc);
//    }

    @Override
    public WriterFunction write(Publisher<? extends Serializable> obj, Type typeOfSrc) {
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
