package io.github.aboodz.summer.blog;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Singleton;

@Singleton
public class BlogHandler {

    public Mono<String> getBlog(HttpServerRequest httpServerRequest) {
        return Mono.just("Hello, this is my blog");
    }

}
