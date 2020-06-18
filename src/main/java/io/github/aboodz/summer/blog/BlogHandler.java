package io.github.aboodz.summer.blog;

import io.github.aboodz.summer.blog.domain.Blog;
import io.github.aboodz.summer.server.ResponseBuilder;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Singleton;

import static io.github.aboodz.summer.server.ResponseBuilder.ok;

@Singleton
public class BlogHandler {

    public ResponseBuilder getBlog(HttpServerRequest httpServerRequest) {
        return ok().body(new Blog("Hello, this is my blog"));
    }

}
