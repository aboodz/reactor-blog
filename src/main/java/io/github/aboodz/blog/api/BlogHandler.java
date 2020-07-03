package io.github.aboodz.blog.api;

import com.google.common.base.Verify;
import com.google.common.primitives.Longs;
import io.github.aboodz.blog.api.exceptions.InvalidIdFormatException;
import io.github.aboodz.blog.dao.PostDao;
import io.github.aboodz.blog.dao.exceptions.EntityNotFoundException;
import io.github.aboodz.blog.domain.Post;
import io.github.aboodz.server.HandlerResult;
import io.github.aboodz.server.serdes.ObjectReader;
import io.github.aboodz.server.serdes.ObjectWriter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.function.Consumer;

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

    public Mono<HandlerResult> getBlog(HttpServerRequest httpServerRequest) {
        Long id = getBlogIdFromRequest(httpServerRequest);

        return postDao.get(id)
                .map(post -> HandlerResult.ok().body(writer.write(post, Post.class)))
                .defaultIfEmpty(HandlerResult.notFound());
    }

    public Mono<HandlerResult> createBlog(HttpServerRequest httpServerRequest) {
        return reader.readObject(httpServerRequest, Post.class)
                .doOnNext(validatePost)
                .flatMap(postDao::insert)
                .map(id -> HandlerResult.created(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", id.toString())));
    }

    public Mono<HandlerResult> updateBlog(HttpServerRequest httpServerRequest) {
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
                .map(result -> HandlerResult.noContent())
                .defaultIfEmpty(HandlerResult.notFound());
    }

    public Mono<HandlerResult> deleteBlog(HttpServerRequest httpServerRequest) {
        Long id = getBlogIdFromRequest(httpServerRequest);

        return postDao.delete(id)
                .thenReturn(HandlerResult.ok())
                .onErrorReturn(EntityNotFoundException.class, HandlerResult.notFound());
    }


}
