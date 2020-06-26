package io.github.aboodz.summer.blog.api.exceptions;

import io.github.aboodz.summer.server.exceptions.ManagedHandlerException;
import io.netty.handler.codec.http.HttpResponseStatus;

public class InvalidIdFormatException extends ManagedHandlerException {


    public InvalidIdFormatException() {
        super(HttpResponseStatus.BAD_REQUEST, "Invalid id format");
    }
}
