package io.github.aboodz.blog.api;


import io.github.aboodz.server.HandlerResolver;
import io.github.aboodz.server.Routable;
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

    @Inject
    BlogRoutes(BlogHandler blogHandler) {
        this.blogHandler = blogHandler;
    }

    @Override
    public void defineRoutes(HttpServerRoutes routes, HandlerResolver resolver) {
        routes.post(POST_PATH, resolver.resolveMono(blogHandler::createBlog));
        routes.get(POST_RESOURCE_PATH, resolver.resolveMono(blogHandler::getBlog));
        routes.put(POST_RESOURCE_PATH, resolver.resolveMono(blogHandler::updateBlog));
        routes.delete(POST_RESOURCE_PATH, resolver.resolveMono(blogHandler::deleteBlog));
    }

}
