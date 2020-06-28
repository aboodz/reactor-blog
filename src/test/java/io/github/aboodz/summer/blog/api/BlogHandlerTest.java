package io.github.aboodz.summer.blog.api;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.aboodz.summer.blog.api.exceptions.InvalidIdFormatException;
import io.github.aboodz.summer.blog.dao.PostDao;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.server.HandlerResolver;
import io.github.aboodz.summer.server.ServerModule;
import io.github.aboodz.summer.server.exceptions.ErrorResponse;
import io.github.aboodz.summer.test.assertions.AssertHttpResponse;
import io.github.aboodz.summer.test.server.ServerFixture;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlogHandlerTest {

    public static final Long EXISTING_POST_ID = 1L;
    public static final Long NON_EXISTING_POST_ID = 2L;
    public static final Long ADDED_POST_ID = 3L;
    public static final Post post = new Post(EXISTING_POST_ID, "test title", "test body", Set.of("keyword"));

    static PostDao postDao;
    static DisposableServer server;
    static HttpClient client;
    static ServerFixture serverFixture;
    static Gson gson;
    static AssertHttpResponse assertHttpResponse;

    @BeforeAll
    static void beforeAll() {
        serverFixture = new ServerFixture();
        Module mockModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(PostDao.class).toInstance(mock(PostDao.class));
            }
        };

        Injector injector = Guice.createInjector(new ServerModule(), mockModule, new BlogApiModule());
        BlogRoutes blogRoutes = injector.getInstance(BlogRoutes.class);
        postDao = injector.getInstance(PostDao.class);
        gson = injector.getInstance(Gson.class);
        HandlerResolver resolver = injector.getInstance(HandlerResolver.class);

        assertHttpResponse = new AssertHttpResponse(gson);

        server = serverFixture.createTestingServer(blogRoutes, resolver);
        client = serverFixture.createTestingClient(server);

        when(postDao.get(EXISTING_POST_ID)).thenReturn(Mono.just(post));
        when(postDao.get(NON_EXISTING_POST_ID)).thenReturn(Mono.empty());
        when(postDao.insert(any(Post.class))).thenReturn(Mono.just(ADDED_POST_ID));
        when(postDao.update(any(Post.class))).thenReturn(Mono.empty());
    }

    @Nested
    class GetPost {

        @Test
        void givenValidIdOfExistingPost_getPost_shouldReturnPost() {
            Mono<Tuple2<HttpClientResponse, String>> response = client.get()
                    .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", EXISTING_POST_ID.toString()))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.zip(Mono.just(httpClientResponse), byteBufMono.asString()));

            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.OK, post, Post.class))
                    .verifyComplete();
        }

        @Test
        void givenInvalidIdOfExistingPost_getPost_shouldError400() {
            Mono<Tuple2<HttpClientResponse, String>> response = client.get()
                    .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", "wrong"))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.zip(Mono.just(httpClientResponse), byteBufMono.asString()));

            ErrorResponse errorResponse = new InvalidIdFormatException().toErrorResponse();
            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.BAD_REQUEST, errorResponse, ErrorResponse.class))
                    .verifyComplete();
        }


        @Test
        void givenValidOfNonExistingPost_getPost_shouldError404() {
            Mono<HttpClientResponse> response = client.get()
                    .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", NON_EXISTING_POST_ID.toString()))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.just(httpClientResponse));

            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.NOT_FOUND))
                    .verifyComplete();
        }

    }

    @Nested
    class CreatePost {

        @Test
        void givenValidPostDetails_createBlog_shouldCreated() {
            String newPostJson = "{\"title\":\"test title\",\"body\":\"Bla bla bla\",\"keywords\":[\"test-keyword\"]}";
            Mono<HttpClientResponse> response = client.post()
                    .uri(BlogRoutes.POST_PATH)
                    .send(ByteBufFlux.fromString(Mono.just(newPostJson)))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.just(httpClientResponse));

            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.combine(
                            assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.CREATED),
                            assertHttpResponse.assertContainsHeader(HttpHeaderNames.LOCATION.toString(), "/post/" + ADDED_POST_ID)
                    ))
                    .verifyComplete();
        }

    }

    @Nested
    class UpdatePost {

        @Test
        void givenValidPostDetails_updateBlog_shouldUpdate() {
            String newPostJson = "{\"title\":\"updated title\",\"body\":\"hello, I have updates\",\"keywords\":[\"another_keyword\"]}";
            Mono<HttpClientResponse> response = client.put()
                    .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", EXISTING_POST_ID.toString()))
                    .send(ByteBufFlux.fromString(Mono.just(newPostJson)))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.just(httpClientResponse));

            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.NO_CONTENT))
                    .verifyComplete();
        }

        @Test
        void givenNonExistingPostId_updateBlog_shouldFail404() {
            String newPostJson = "{\"title\":\"updated title\",\"body\":\"hello, I have updates\",\"keywords\":[\"another_keyword\"]}";
            Mono<HttpClientResponse> response = client.put()
                    .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", NON_EXISTING_POST_ID.toString()))
                    .send(ByteBufFlux.fromString(Mono.just(newPostJson)))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.just(httpClientResponse));

            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.NOT_FOUND))
                    .verifyComplete();
        }

        @Test
        void givenInvalidId_updateBlog_shouldFail400() {
            String newPostJson = "{\"title\":\"updated title\",\"body\":\"hello, I have updates\",\"keywords\":[\"another_keyword\"]}";
            Mono<HttpClientResponse> response = client.put()
                    .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", "wrong"))
                    .send(ByteBufFlux.fromString(Mono.just(newPostJson)))
                    .responseSingle((httpClientResponse, byteBufMono) -> Mono.just(httpClientResponse));

            StepVerifier.create(response)
                    .assertNext(assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.BAD_REQUEST))
                    .verifyComplete();
        }



    }


    @AfterAll
    static void afterAll() {
        server.disposeNow();
    }
}
