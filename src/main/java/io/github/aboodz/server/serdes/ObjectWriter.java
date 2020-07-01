package io.github.aboodz.server.serdes;

import org.reactivestreams.Publisher;

import java.io.Serializable;

public interface ObjectWriter {

    <T extends Serializable> WriterFunction write(T obj);
    <T extends Serializable> WriterFunction write(T obj, Class<T> typeOfSrc);
    <T extends Serializable> WriterFunction write(Publisher<T> obj, Class<T> typeOfSrc);

}
