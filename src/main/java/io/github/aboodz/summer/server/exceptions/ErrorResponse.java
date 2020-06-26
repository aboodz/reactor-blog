package io.github.aboodz.summer.server.exceptions;

import lombok.Value;


@Value
public class ErrorResponse {

    ErrorResponse(Integer status, String message, Object error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    Integer status;
    String message;
    Object error;
}
