package io.github.aboodz.summer.blog;


import io.github.aboodz.summer.server.DefaultHandlerResolver;
import io.github.aboodz.summer.server.HandlerResolver;
import io.github.aboodz.summer.server.Routable;
import lombok.extern.log4j.Log4j2;
import reactor.netty.http.server.HttpServerRoutes;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class BlogRoutes implements Routable {

    private final BlogHandler blogHandler;
    private final HandlerResolver resolver = new DefaultHandlerResolver();

    @Inject
    BlogRoutes(BlogHandler blogHandler) {
        this.blogHandler = blogHandler;
    }

    @Override
    public void defineRoutes(HttpServerRoutes routes) {
        routes.get("/blog", resolver.resolve(blogHandler::getBlog));
    }

}
