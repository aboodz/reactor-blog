package io.github.aboodz.summer.blog.dao;

import io.github.aboodz.summer.blog.domain.Post;
import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

@Testcontainers
class PostDaoTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12.3-alpine")
            .withClasspathResourceMapping("./sql/1-init.sql", "/docker-entrypoint-initdb.d/1.sql", BindMode.READ_ONLY)
            .withClasspathResourceMapping("./sql/posts-data.sql", "/docker-entrypoint-initdb.d/2.sql", BindMode.READ_ONLY)
            .withStartupTimeoutSeconds(5);

    private static R2dbc r2dbc;

    @BeforeAll
    static void beforeAll() {
        PostgreSQLR2DBCDatabaseContainer postgreSQLR2DBCDatabaseContainer = new PostgreSQLR2DBCDatabaseContainer(postgreSQLContainer);
        ConnectionFactoryOptions options = PostgreSQLR2DBCDatabaseContainer.getOptions(postgreSQLContainer);
        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactoryProvider().create(options);
        r2dbc = new R2dbc(postgresqlConnectionFactory);
        postgreSQLR2DBCDatabaseContainer.start();
    }

    @Test
    void givenAnExistingItemId_get_shouldEmitTheEntity() {
        Mono<Post> postMono = new PostDao(r2dbc).get(1L);
        StepVerifier.create(postMono)
                .expectNextMatches(post -> {
                    Assertions.assertNotNull(post);
                    Assertions.assertEquals(1, post.getId());
                    Assertions.assertEquals("a new moon is rising", post.getTitle());
                    Assertions.assertEquals(Set.of("technology"), post.getKeywords());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingItemId_get_shouldEmitError() {
        Mono<Post> postMono = new PostDao(r2dbc).get(2L);
        StepVerifier.create(postMono)
                .verifyComplete();
    }

    @Test
    void insert() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

}
