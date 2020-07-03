package io.github.aboodz.server.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Value;

import java.io.Serializable;


@Value
public class ErrorResponse implements Serializable {

    public static ErrorResponse technicalError(HttpResponseStatus status) {
        return new ErrorResponse(status.code(), status.reasonPhrase(), "Something went wrong");
    }

    Integer status;
    String message;
    Object error;
}
