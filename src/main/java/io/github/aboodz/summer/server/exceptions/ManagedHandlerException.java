package io.github.aboodz.summer.server.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

@Getter
public class ManagedHandlerException extends RuntimeException {

    private final HttpResponseStatus status;

    public ManagedHandlerException(HttpResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(status.code(), status.reasonPhrase(), getMessage());
    }

}
