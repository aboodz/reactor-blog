package io.github.aboodz.summer.blog;

import io.github.aboodz.summer.blog.domain.Blog;
import io.github.aboodz.summer.server.HandlerResult;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Singleton;

import static io.github.aboodz.summer.server.HandlerResult.ok;

@Singleton
public class BlogHandler {

    public HandlerResult getBlog(HttpServerRequest httpServerRequest) {
        return ok().body(new Blog("Hello, this is my blog"));
    }

}
