package io.github.aboodz.summer.server.serdes.gson;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.aboodz.summer.server.serdes.ObjectReader;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.io.Serializable;

public class GsonObjectReader implements ObjectReader {

    private final Gson gson;

    @Inject
    public GsonObjectReader(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T extends Serializable> Mono<T> readObject(HttpServerRequest request, Class<T> clazz) {
        return request.receive()
                .asInputStream()
                .map(inputStream -> {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    JsonReader jsonReader = gson.newJsonReader(inputStreamReader);
                    return gson.<T>fromJson(jsonReader, clazz);
                })
                .single();
    }
}
