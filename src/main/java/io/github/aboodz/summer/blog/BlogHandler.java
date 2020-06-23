package io.github.aboodz.summer.blog;

import io.github.aboodz.summer.blog.dao.PostDao;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.server.HandlerResult;
import io.github.aboodz.summer.server.serdes.GsonResultWriter;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.github.aboodz.summer.server.HandlerResult.ok;

@Singleton
public class BlogHandler {

    private final GsonResultWriter writer;
    private final PostDao postDao;

    @Inject
    public BlogHandler(GsonResultWriter writer, PostDao postDao) {
        this.writer = writer;
        this.postDao = postDao;
    }

    public HandlerResult getBlog(HttpServerRequest httpServerRequest) {
        return ok().body(writer.write(postDao.get(1L), Post.class));
    }

}
