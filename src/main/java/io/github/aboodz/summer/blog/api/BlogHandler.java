package io.github.aboodz.summer.blog.api;

import com.google.common.primitives.Longs;
import io.github.aboodz.summer.blog.api.exceptions.InvalidIdFormatException;
import io.github.aboodz.summer.blog.dao.PostDao;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.server.HandlerFunction;
import io.github.aboodz.summer.server.serdes.GsonResultWriter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Optional;

import static io.github.aboodz.summer.server.HandlerFunction.notFound;
import static io.github.aboodz.summer.server.HandlerFunction.ok;

@Singleton
public class BlogHandler {

    private final GsonResultWriter writer;
    private final PostDao postDao;

    @Inject
    BlogHandler(GsonResultWriter writer, PostDao postDao) {
        this.writer = writer;
        this.postDao = postDao;
    }

    public Mono<HandlerFunction> getBlog(HttpServerRequest httpServerRequest) {
        Long id = Optional.ofNullable(httpServerRequest.param("id"))
                .flatMap(stringId -> Optional.ofNullable(Longs.tryParse(stringId)))
                .orElseThrow(InvalidIdFormatException::new);

        return postDao.get(id)
                .map(post -> ok().body(writer.write(post, Post.class)))
                .defaultIfEmpty(notFound());
    }

    public HandlerFunction postBlog(HttpServerRequest httpServerRequest) {
        throw new UnsupportedOperationException();
    }

    public HandlerFunction updateBlog(HttpServerRequest httpServerRequest) {
        throw new UnsupportedOperationException();
    }


}
