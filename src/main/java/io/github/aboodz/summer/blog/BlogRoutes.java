package io.github.aboodz.summer.blog;


import com.google.inject.multibindings.ProvidesIntoSet;
import io.github.aboodz.summer.server.Routable;
import io.github.aboodz.summer.server.Router;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRoutes;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class BlogRoutes implements Routable {

    private final BlogHandler blogHandler;

    @Inject
    BlogRoutes(BlogHandler blogHandler) {
        this.blogHandler = blogHandler;
    }

    @Override
    public void defineRoutes(HttpServerRoutes routes) {
        routes.get("/blog", (httpServerRequest, httpServerResponse) -> {
            Mono<String> blog = blogHandler.getBlog(httpServerRequest);
            return httpServerResponse.sendString(blog);
        });
    }
}
