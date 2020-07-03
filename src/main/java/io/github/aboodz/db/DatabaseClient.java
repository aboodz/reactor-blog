package io.github.aboodz.db;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.function.Function;

public class DatabaseClient {

    private final ConnectionPool connectionPool;

    @Inject
    DatabaseClient(DatabaseConnection databaseConnection) {
        this.connectionPool = databaseConnection.getPool();
    }

    public DatabaseClient(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public <T> Flux<T> inTransactional(Function<Connection, ? extends Publisher<? extends T>> transactions) {
        return connectionPool.create()
                .flatMapMany(connection -> {
                    Mono<T> begin = Mono.fromDirect(connection.beginTransaction()).then(Mono.empty());
                    Mono<T> commit = Mono.fromDirect(connection.commitTransaction()).then(Mono.empty());
                    Function<Throwable, Publisher<T>> rollback = e -> Mono.fromDirect(connection.rollbackTransaction()).then(Mono.error(e));
                    return Flux.concat(begin, transactions.apply(connection), commit)
                            .onErrorResume(rollback)
                            .doFinally(signalType -> connection.close());
                });
    }

}
