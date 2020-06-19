package io.github.aboodz.summer.blog;

import io.github.aboodz.summer.blog.domain.Blog;
import io.github.aboodz.summer.server.HandlerResult;
import io.github.aboodz.summer.server.serdes.GsonResultWriter;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.github.aboodz.summer.server.HandlerResult.ok;

@Singleton
public class BlogHandler {

    private final GsonResultWriter writer;

    @Inject
    public BlogHandler(GsonResultWriter writer) {
        this.writer = writer;
    }

    public HandlerResult getBlog(HttpServerRequest httpServerRequest) {
        return ok().body(writer.write(new Blog("Hello, this is my blog")));
    }

}
