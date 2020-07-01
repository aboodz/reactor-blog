package io.github.aboodz.blog.dao;

import io.github.aboodz.blog.dao.exceptions.EntityNotFoundException;
import io.github.aboodz.blog.dao.exceptions.NonUniqueItemException;
import io.github.aboodz.blog.domain.Post;
import io.github.aboodz.db.DatabaseClient;
import io.r2dbc.spi.Result;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashSet;

@Singleton
@Log4j2
public class PostDao implements ReactiveDao<Post, Long> {

    private final DatabaseClient client;

    @Inject
    PostDao(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Mono<Post> get(Long id) {
        return client.inTransactional(connection -> Mono.from(
                connection.createStatement("SELECT * FROM POSTS WHERE ID = $1")
                        .bind("$1", id)
                        .execute()
        ).flatMap(
                result -> Mono.fromDirect(result.getRowsUpdated())
                        .flatMap(numberOfItems -> {
                            if (numberOfItems == 0) return Mono.empty();
                            if (numberOfItems > 1) return Mono.error(NonUniqueItemException::new);
                            // TODO: maybe we can pass this as a mapping function that takes result and returns object
                            return Mono.from(result.map((row, rowMetadata) -> new Post(
                                    row.get("id", Long.class),
                                    row.get("title", String.class),
                                    row.get("body", String.class),
                                    new HashSet<>(Arrays.asList(row.get("keywords", String[].class)))
                            )));
                        })
        )).publishNext();
    }

    @Override
    public Mono<Long> insert(Post entity) {
        return client.inTransactional(
                connection -> Mono
                        .from(connection.createStatement("select nextval('posts_id_seq')").execute())
                        .flatMap(result -> Mono.from(result.map((row, rowMetadata) -> row.get(0, Long.class))))
                        .flatMap(id -> Mono.from(connection.createStatement("INSERT INTO POSTS VALUES ($1, $2, $3, $4)")
                                .bind("$1", id)
                                .bind("$2", entity.getTitle())
                                .bind("$3", entity.getBody())
                                .bind("$4", entity.getKeywords().toArray(new String[0]))
                                .execute())
                                .flatMap(result -> Mono.fromDirect(result.getRowsUpdated()))
                                .doOnNext(affectedRows -> {
                                    if (affectedRows == 0) {
                                        throw new EntityNotFoundException();
                                    }
                                })
                                .then(Mono.just(id))
                        )
        ).single();
    }

    @Override
    public Mono<Void> update(Post entity) {
        return client.inTransactional(
                connection ->
                        Flux.from(connection.createStatement("update posts set title = $2, body = $3, keywords = $4 where id = $1")
                                .bind("$1", entity.getId())
                                .bind("$2", entity.getTitle())
                                .bind("$3", entity.getBody())
                                .bind("$4", entity.getKeywords().toArray(new String[0]))
                                .execute()).flatMap(Result::getRowsUpdated)
        ).doOnNext(affectedRows -> {
            if (affectedRows == 0) {
                throw new EntityNotFoundException();
            }
        }).then();
    }

    @Override
    public Mono<Void> delete(Long id) {
        return client.inTransactional(
                connection -> connection.createStatement("delete from posts where id = $1")
                        .bind("$1", id)
                        .execute()
        ).flatMap(Result::getRowsUpdated)
                .doOnNext(affectedRows -> {
                    if (affectedRows == 0) {
                        throw new EntityNotFoundException();
                    }
                }).then();
    }
}
