package io.github.aboodz.summer.blog.api;

import com.google.common.base.Verify;
import com.google.common.primitives.Longs;
import io.github.aboodz.summer.blog.api.exceptions.InvalidIdFormatException;
import io.github.aboodz.summer.blog.dao.PostDao;
import io.github.aboodz.summer.blog.dao.exceptions.EntityNotFoundException;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.server.HandlerFunction;
import io.github.aboodz.summer.server.serdes.ObjectReader;
import io.github.aboodz.summer.server.serdes.ObjectWriter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.function.Consumer;

import static io.github.aboodz.summer.blog.api.BlogRoutes.POST_RESOURCE_PATH;
import static io.github.aboodz.summer.server.HandlerFunction.*;

@Singleton
public class BlogHandler {

    private final ObjectWriter writer;
    private final ObjectReader reader;
    private final PostDao postDao;

    private final Consumer<Post> validatePost = post -> {
        Verify.verifyNotNull(post.getTitle());
        Verify.verifyNotNull(post.getBody());
    };

    @Inject
    BlogHandler(ObjectWriter writer, ObjectReader reader, PostDao postDao) {
        this.writer = writer;
        this.reader = reader;
        this.postDao = postDao;
    }

    private Long getBlogIdFromRequest(HttpServerRequest httpServerRequest) {
        return Optional.ofNullable(httpServerRequest.param("id"))
                .flatMap(stringId -> Optional.ofNullable(Longs.tryParse(stringId)))
                .orElseThrow(InvalidIdFormatException::new);
    }

    public Mono<HandlerFunction> getBlog(HttpServerRequest httpServerRequest) {
        Long id = getBlogIdFromRequest(httpServerRequest);

        return postDao.get(id)
                .map(post -> ok().body(writer.write(post, Post.class)))
                .defaultIfEmpty(notFound());
    }

    public Mono<HandlerFunction> createBlog(HttpServerRequest httpServerRequest) {
        return reader.readObject(httpServerRequest, Post.class)
                .doOnNext(validatePost)
                .flatMap(postDao::insert)
                .map(id -> created(POST_RESOURCE_PATH.replace("{id}", id.toString())));
    }

    public Mono<HandlerFunction> updateBlog(HttpServerRequest httpServerRequest) {
        Long id = getBlogIdFromRequest(httpServerRequest);

        return reader.readObject(httpServerRequest, Post.class)
                .doOnNext(validatePost)
                .zipWith(postDao.get(id))
                .map(postAndUpdateModel -> {
                    Post currentPost = postAndUpdateModel.getT2();
                    Post newPost = postAndUpdateModel.getT1();
                    return currentPost.withTitle(newPost.getTitle())
                            .withBody(newPost.getBody())
                            .withKeywords(newPost.getKeywords());
                })
                .flatMap(post -> postDao.update(post).thenReturn(true))
                .map(result -> noContent())
                .defaultIfEmpty(notFound());
    }

    public Mono<HandlerFunction> deleteBlog(HttpServerRequest httpServerRequest) {
        Long id = getBlogIdFromRequest(httpServerRequest);

        return postDao.delete(id)
                .thenReturn(ok())
                .onErrorReturn(EntityNotFoundException.class, notFound());
    }


}
