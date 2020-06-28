package io.github.aboodz.summer.blog.api;

import com.google.common.primitives.Longs;
import io.github.aboodz.summer.blog.api.exceptions.InvalidIdFormatException;
import io.github.aboodz.summer.blog.dao.PostDao;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.server.HandlerFunction;
import io.github.aboodz.summer.server.serdes.ObjectWriter;
import io.github.aboodz.summer.server.serdes.ObjectReader;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static io.github.aboodz.summer.blog.api.BlogRoutes.POST_RESOURCE_PATH;
import static io.github.aboodz.summer.server.HandlerFunction.*;

@Singleton
public class BlogHandler {

    private final ObjectWriter writer;
    private final ObjectReader reader;
    private final PostDao postDao;

    @Inject
    BlogHandler(ObjectWriter writer, ObjectReader reader, PostDao postDao) {
        this.writer = writer;
        this.reader = reader;
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

    public Mono<HandlerFunction> createBlog(HttpServerRequest httpServerRequest) {
        return reader.readObject(httpServerRequest, Post.class)
                .flatMap(postDao::insert)
                .map(id -> created(POST_RESOURCE_PATH.replace("{id}", id.toString())));
    }

    public HandlerFunction updateBlog(HttpServerRequest httpServerRequest) {
        throw new UnsupportedOperationException();
    }


}
