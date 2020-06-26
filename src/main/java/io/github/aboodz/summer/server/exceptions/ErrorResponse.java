package io.github.aboodz.summer.server.exceptions;

import lombok.Value;


@Value
public class ErrorResponse {
    Integer status;
    String message;
    Object error;
}
