package io.github.aboodz.summer.blog.api;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.aboodz.summer.blog.dao.PostDao;
import io.github.aboodz.summer.blog.domain.Post;
import io.github.aboodz.summer.server.ServerModule;
import io.github.aboodz.summer.test.assertions.AssertHttpResponse;
import io.github.aboodz.summer.test.server.ServerFixture;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlogHandlerTest {

    public static final Long VALID_POST_ID = 1L;
    public static final Post post = new Post(VALID_POST_ID, "test title", "test body", Set.of("keyword"));

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

        assertHttpResponse = new AssertHttpResponse(gson);

        server = serverFixture.createTestingServer(blogRoutes);
        client = serverFixture.createTestingClient(server);

        when(postDao.get(VALID_POST_ID)).thenReturn(Mono.just(post));
    }

    @Test
    void givenValidIdOfExistingPost_getPost_shouldReturnPost() {
        Mono<Tuple2<HttpClientResponse, String>> response = client.get()
                .uri(BlogRoutes.POST_RESOURCE_PATH.replace("{id}", VALID_POST_ID.toString()))
                .responseSingle((httpClientResponse, byteBufMono) -> Mono.zip(Mono.just(httpClientResponse), byteBufMono.asString()));

        StepVerifier.create(response)
                .assertNext(r -> assertHttpResponse.assertHttpResponseEquals(HttpResponseStatus.OK, post, Post.class))
                .verifyComplete();
    }


    @AfterAll
    static void afterAll() {
        server.disposeNow();
    }
}
