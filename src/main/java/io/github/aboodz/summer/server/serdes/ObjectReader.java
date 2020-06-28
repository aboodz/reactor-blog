package io.github.aboodz.summer.server.serdes;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import java.io.Serializable;

public interface ObjectReader {

    <T extends Serializable> Mono<T> readObject(HttpServerRequest request, Class<T> clazz);

}
