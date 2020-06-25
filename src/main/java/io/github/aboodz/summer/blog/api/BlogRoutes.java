package io.github.aboodz.summer.blog.api;


import io.github.aboodz.summer.server.HandlerResolver;
import io.github.aboodz.summer.server.Routable;
import lombok.extern.log4j.Log4j2;
import reactor.netty.http.server.HttpServerRoutes;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class BlogRoutes implements Routable {

    public static final String POST_PATH = "/post";
    public static final String POST_RESOURCE_PATH = POST_PATH + "/{id}";

    private final BlogHandler blogHandler;
    private final HandlerResolver resolver;

    @Inject
    BlogRoutes(BlogHandler blogHandler, HandlerResolver resolver) {
        this.blogHandler = blogHandler;
        this.resolver = resolver;
    }

    @Override
    public void defineRoutes(HttpServerRoutes routes) {
        routes.get(POST_RESOURCE_PATH, resolver.resolveMono(blogHandler::getBlog));
        routes.post("/blog/{id}", resolver.resolveMono(blogHandler::getBlog));
    }

}
