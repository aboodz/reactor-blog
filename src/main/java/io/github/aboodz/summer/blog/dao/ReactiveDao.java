package io.github.aboodz.summer.blog.dao;

import reactor.core.publisher.Mono;

public interface ReactiveDao<T> {

    Mono<T> get(Long id);
    Mono<Void> insert(T entity);
    Mono<Void> update(T entity);
    Mono<Void> delete(Long id);

}
