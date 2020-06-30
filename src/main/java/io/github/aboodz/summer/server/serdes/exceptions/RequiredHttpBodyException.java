package io.github.aboodz.summer.server.serdes.exceptions;

import io.github.aboodz.summer.server.exceptions.ManagedHandlerException;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RequiredHttpBodyException extends ManagedHandlerException {

    public RequiredHttpBodyException(Class<?> clazz) {
        super(HttpResponseStatus.BAD_REQUEST,String.format("Could not deserialize input stream to %s",clazz.getSimpleName()));
    }

}
