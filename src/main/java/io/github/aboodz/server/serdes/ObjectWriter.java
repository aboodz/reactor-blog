package io.github.aboodz.server.serdes;

import io.github.aboodz.server.HandlerResult;

import java.io.Serializable;

public interface ObjectWriter {

    <T extends Serializable> HandlerResult.BodyWriter write(T obj);
    <T extends Serializable> HandlerResult.BodyWriter write(T obj, Class<T> typeOfSrc);

}
