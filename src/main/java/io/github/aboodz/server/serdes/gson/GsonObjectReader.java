package io.github.aboodz.server.serdes.gson;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.aboodz.server.serdes.ObjectReader;
import io.github.aboodz.server.serdes.exceptions.RequiredHttpBodyException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.io.Serializable;

@Log4j2
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
                .switchIfEmpty(Mono.error(new RequiredHttpBodyException(clazz)))
                .single();
    }
}
