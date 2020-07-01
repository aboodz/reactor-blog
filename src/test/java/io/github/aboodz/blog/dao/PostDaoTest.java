package io.github.aboodz.blog.dao;

import io.github.aboodz.blog.domain.Post;
import io.github.aboodz.blog.dao.exceptions.EntityNotFoundException;
import io.github.aboodz.db.DatabaseClient;
import io.github.aboodz.test.db.DatabaseFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PostDaoTest {

    private static DatabaseClient databaseClient;

    @BeforeAll
    static void beforeAll() {
        databaseClient = new DatabaseFixture().startAndSchemaDatabase();
    }

    @Nested
    class GetTest {

        @Test
        void givenAnExistingItemId_get_shouldEmitTheEntity() {
            Mono<Post> postMono = new PostDao(databaseClient).get(1L);

            StepVerifier.create(postMono)
                    .assertNext(post -> {
                        assertNotNull(post);
                        assertEquals(1L, post.getId());
                        assertEquals("a new moon is rising", post.getTitle());
                        assertEquals(Set.of("technology"), post.getKeywords());
                    })
                    .expectComplete()
                    .verify();
        }

        @Test
        void givenNonExistingItemId_get_shouldComplete() {
            Mono<Post> postMono = new PostDao(databaseClient).get(2L);
            StepVerifier.create(postMono)
                    .verifyComplete();
        }

    }

    @Nested
    class InsertTest {

        @Test
        void givenPost_insert_shouldInsertPost() {
            PostDao postDao = new PostDao(databaseClient);
            Post expectedPost = new Post(null, "test title", "test body", Set.of("keyword"));

            Mono<Long> id = postDao.insert(expectedPost);

            Mono<Post> actualPost = Mono.from(id).flatMap(postDao::get);

            StepVerifier.create(actualPost)
                    .assertNext(p -> {
                        assertNotNull(p.getId(), "id should auto generate");
                        assertEquals(expectedPost.withId(p.getId()), p);
                    })
                    .expectComplete()
                    .verify();
        }

        @Test
        void givenPostWithNonNullId_insert_shouldInsertPostIgnoringTheId() {
            PostDao postDao = new PostDao(databaseClient);
            Post expectedPost = new Post(4L, "test title", "test body", Set.of("keyword"));

            Mono<Post> actualPost = postDao.insert(expectedPost)
                    .flatMap(postDao::get);

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
            PostDao postDao = new PostDao(databaseClient);
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
            PostDao postDao = new PostDao(databaseClient);

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
            PostDao postDao = new PostDao(databaseClient);

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
            PostDao postDao = new PostDao(databaseClient);

            Mono<Post> actualPost = postDao.delete(99L)
                    .then(postDao.get(99L));

            StepVerifier.create(actualPost)
                    .verifyError(EntityNotFoundException.class);
        }

    }


}
