package io.github.aboodz.blog.dao;

import reactor.core.publisher.Mono;

public interface ReactiveDao<T, KEY> {

    Mono<T> get(KEY id);
    Mono<KEY> insert(T entity);
    Mono<Void> update(T entity);
    Mono<Void> delete(KEY id);

}
