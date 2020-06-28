package io.github.aboodz.summer.server.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Value;


@Value
public class ErrorResponse {

    public static ErrorResponse technicalError(HttpResponseStatus status) {
        return new ErrorResponse(status.code(), status.reasonPhrase(), "Something went wrong");
    }

    Integer status;
    String message;
    Object error;
}
