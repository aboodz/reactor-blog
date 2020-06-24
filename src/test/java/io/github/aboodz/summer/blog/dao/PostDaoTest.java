package io.github.aboodz.summer.blog.dao;

import io.github.aboodz.summer.blog.dao.exceptions.EntityNotFoundException;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.test.db.DatabaseFixture;
import io.r2dbc.client.R2dbc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

@Testcontainers
class PostDaoTest {

    private static R2dbc r2dbc;

    @BeforeAll
    static void beforeAll() {
        r2dbc = new DatabaseFixture().startAndSchemaDatabase();
    }

    @Nested
    class GetTest {

        @Test
        void givenAnExistingItemId_get_shouldEmitTheEntity() {
            Mono<Post> postMono = new PostDao(r2dbc).get(1L);

            StepVerifier.create(postMono)
                    .expectNextMatches(post -> {
                        Assertions.assertNotNull(post);
                        Assertions.assertEquals(1L, post.getId());
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

    }

    @Nested
    class InsertTest {

        @Test
        void givenPost_insert_shouldInsertPost() {
            PostDao postDao = new PostDao(r2dbc);
            Post expectedPost = new Post(null, "test title", "test body", Set.of("keyword"));

            Mono<Long> id = postDao.insert(expectedPost);

            Mono<Post> actualPost = Mono.from(id).flatMap(postDao::get);

            StepVerifier.create(actualPost)
                    .expectNextMatches(p -> {
                        Assertions.assertNotNull(p.getId(), "id should auto generate");
                        return expectedPost.withId(p.getId()).equals(p);
                    })
                    .expectComplete()
                    .verify();
        }

        @Test
        void givenPostWithNonNullId_insert_shouldInsertPostIgnoringTheId() {
            PostDao postDao = new PostDao(r2dbc);
            Post expectedPost = new Post(4L, "test title", "test body", Set.of("keyword"));

            Mono<Post> actualPost = postDao.insert(expectedPost)
                    .flatMap(postDao::get); // why not

            StepVerifier.create(actualPost)
                    .expectNextMatches(post -> expectedPost.withId(post.getId()).equals(post))
                    .expectComplete()
                    .verify();
        }


    }


    @Nested
    class UpdateTest {

        @Test
        void givenExistingPost_update_shouldUpdateChangedFields() {
            PostDao postDao = new PostDao(r2dbc);
            Post updated = postDao.get(1L)
                    .block()
                    .withBody("changed body");

            Mono<Post> actualPost = postDao.update(updated)
                    .then(postDao.get(updated.getId()));

            StepVerifier.create(actualPost)
                    .expectNext(updated)
                    .verifyComplete();
        }

        @Test
        void givenNonExistingPost_update_shouldFail() {
            PostDao postDao = new PostDao(r2dbc);

            Post post = new Post(99L, "test title", "test body", Set.of("java"));

            Mono<Void> actual = postDao.update(post);

            StepVerifier.create(actual)
                    .verifyError(EntityNotFoundException.class);
        }
    }


    @Nested
    class DeleteTest {

        @Test
        void givenExistingPost_delete_shouldDelete() {
            PostDao postDao = new PostDao(r2dbc);

            Post post = new Post(null, "test title", "test body", Set.of("test"));

            // insert a post then remove it
            Mono<Post> actualPost = postDao.insert(post).flatMap(
                    id -> postDao.delete(id).then(postDao.get(id))
            );

            StepVerifier.create(actualPost)
                    .verifyComplete();
        }

        @Test
        void givenNonExistingPost_delete_shouldDoNothing() {
            PostDao postDao = new PostDao(r2dbc);

            Mono<Post> actualPost = postDao.delete(99L)
                    .then(postDao.get(99L));

            StepVerifier.create(actualPost)
                    .verifyComplete();
        }

    }


}
