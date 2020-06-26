package io.github.aboodz.summer.test.assertions;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.netty.http.client.HttpClientResponse;
import reactor.util.function.Tuple2;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class AssertHttpResponse {

    private final Gson gson;

    public AssertHttpResponse(Gson gson) {
        this.gson = gson;
    }

    public Consumer<HttpClientResponse> assertHttpResponseEquals(HttpResponseStatus status) {
        return response -> assertEquals(status, response.status());
    }

    public <T> Consumer<Tuple2<HttpClientResponse, String>> assertHttpResponseEquals(HttpResponseStatus status, T expected, Class<T> clazz) {
        return response -> {
            assertEquals(status, response.getT1().status());

            T responseBody = gson.fromJson(response.getT2(), clazz);
            assertEquals(expected, responseBody);
        };
    }

}
