package io.github.aboodz.summer.blog.dao;

import io.github.aboodz.summer.blog.dao.exceptions.EntityNotFoundException;
import io.github.aboodz.summer.blog.dao.exceptions.NonUniqueItemException;
import io.github.aboodz.summer.blog.domain.Post;
import io.r2dbc.client.R2dbc;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashSet;

@Singleton
@Log4j2
public class PostDao implements ReactiveDao<Post, Long> {

    private final R2dbc r2dbc;

    @Inject
    PostDao(R2dbc r2dbc) {
        this.r2dbc = r2dbc;
    }

    @Override
    public Mono<Post> get(Long id) {
        return r2dbc.inTransaction(
                // TODO: maybe I can pass the query from outside somehow and make this as generic function
                handle -> handle.select("SELECT * FROM POSTS WHERE ID = $1", id)
                        .mapResult(result -> Mono.from(result.getRowsUpdated())
                                .flatMapMany(numberOfItems -> {
                                    if (numberOfItems == 0) return Mono.empty();
                                    if (numberOfItems > 1) return Mono.error(NonUniqueItemException::new);
                                    // TODO: maybe we can pass this as a mapping function that takes result and returns object
                                    return result.map((row, rowMetadata) -> new Post(
                                            row.get("id", Long.class),
                                            row.get("body", String.class),
                                            row.get("title", String.class),
                                            new HashSet<>(Arrays.asList(row.get("keywords", String[].class)))
                                    ));
                                }))
        ).publishNext();
    }

    @Override
    public Mono<Long> insert(Post entity) {
        return r2dbc.inTransaction(
                handle -> handle.select("select nextval('posts_id_seq')")
                        .mapRow(row -> row.get(0, Long.class))
                        .single()
                        .flatMap(id ->
                                handle.execute("INSERT INTO POSTS VALUES ($1, $2, $3, $4)",
                                        id,
                                        entity.getTitle(),
                                        entity.getBody(),
                                        entity.getKeywords().toArray(new String[0])
                                ).map(i -> id).single()
                        )
        ).single();
    }

    @Override
    public Mono<Void> update(Post entity) {
        return r2dbc.inTransaction(
                handle -> handle.execute("update posts set title = $1, body = $2, keywords = $3 where id = $4",
                        entity.getTitle(),
                        entity.getBody(),
                        entity.getKeywords().toArray(new String[0]),
                        entity.getId()
                ).doOnNext(affectedRows -> {
                    if (affectedRows == 0) {
                        throw new EntityNotFoundException();
                    }
                })
        ).single().then();
    }

    @Override
    public Mono<Void> delete(Long id) {
        return r2dbc.inTransaction(
                handle -> handle.execute("delete from posts where id = $1", id).single()
        ).single().then();
    }
}
