package io.github.aboodz.summer.blog.dao;

import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.test.db.DatabaseFixture;
import io.r2dbc.client.R2dbc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

@Testcontainers
class PostDaoTest {

    private R2dbc r2dbc;

    @BeforeEach
    void setUp() {
        this.r2dbc = new DatabaseFixture().startAndSchemaDatabase();
    }

    @Nested
    public class GetTest {

        @Test
        public void givenAnExistingItemId_get_shouldEmitTheEntity() {
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
        public void givenNonExistingItemId_get_shouldEmitError() {
            Mono<Post> postMono = new PostDao(r2dbc).get(2L);
            StepVerifier.create(postMono)
                    .verifyComplete();
        }

    }

    @Nested
    public class InsertTest {

        @Test
        public void givenPost_insert_shouldInsertPost() {
            PostDao postDao = new PostDao(r2dbc);
            Post post = new Post(null, "test", "test", Set.of("test"));

            Mono<Long> id = postDao.insert(post);

            Post expectedPost = id.map(post::withId).block();
            Mono<Post> actualPost = id.flatMap(postDao::get);

            StepVerifier.create(actualPost)
                    .expectNext(expectedPost)
                    .expectComplete()
                    .verify();
        }

        @Test
        public void givenPostWithNonNullId_insert_shouldInsertPostIgnoringTheId() {
            PostDao postDao = new PostDao(r2dbc);
            Post expectedPost = new Post(4L, "test", "test", Set.of("test"));

            Mono<Post> actualPost = postDao.insert(expectedPost)
                    .flatMap(postDao::get); // why not

            StepVerifier.create(actualPost)
                    .expectNextMatches(post -> expectedPost.withId(post.getId()).equals(post))
                    .expectComplete()
                    .verify();
        }


    }


    @Nested
    public class UpdateTest {

        @Test
        void update() {
            PostDao postDao = new PostDao(r2dbc);

            postDao.get(1L);
        }


    }


    @Test
    void delete() {
    }

}