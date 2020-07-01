package io.github.aboodz.test.assertions;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.netty.http.client.HttpClientResponse;
import reactor.util.function.Tuple2;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AssertHttpResponse {

    private final Gson gson;

    public AssertHttpResponse(Gson gson) {
        this.gson = gson;
    }

    public Consumer<HttpClientResponse> assertHttpResponseEquals(HttpResponseStatus status) {
        return response -> assertEquals(status, response.status());
    }

    public Consumer<HttpClientResponse> assertHttpResponseEquals(HttpResponseStatus status, HttpHeaders headers) {
        return response -> {
            assertEquals(status, response.status());
            assertTrue(headers.entries()
                    .stream()
                    .allMatch(header -> response.responseHeaders().contains(header.getKey())
                            && response.responseHeaders().get(header.getKey()).equals(header.getValue())), "headers does not matach");
        };
    }

    public <T> Consumer<Tuple2<HttpClientResponse, String>> assertHttpResponseEquals(HttpResponseStatus status, T expected, Class<T> clazz) {
        return response -> {
            assertEquals(status, response.getT1().status());

            T responseBody = gson.fromJson(response.getT2(), clazz);
            assertEquals(expected, responseBody);
        };
    }

    public Consumer<HttpClientResponse> assertContainsHeader(String headerName, String headerValue) {
        return response -> {
            HttpHeaders headers = response.responseHeaders();
            assertTrue(headers.contains(headerName), String.format("header %s is not found in response", headerName));
            assertEquals(headers.get(headerName), headerValue);
        };
    }

    public Consumer<HttpClientResponse> combine(Consumer<HttpClientResponse> c1, Consumer<HttpClientResponse> c2) {
        return response -> c1.andThen(c2).accept(response);
    }

    public Consumer<HttpClientResponse> combine(Consumer<HttpClientResponse> c1, Consumer<HttpClientResponse> c2, Consumer<HttpClientResponse> c3) {
        return response -> c1.andThen(c2).andThen(c3).accept(response);
    }


}
