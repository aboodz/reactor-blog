package io.github.aboodz.summer.server.serdes;

import org.reactivestreams.Publisher;

import java.io.Serializable;
import java.lang.reflect.Type;

public interface ObjectWriter {

    <T extends Serializable> WriterFunction write(T obj);
    <T extends Serializable> WriterFunction write(T obj, Class<T> typeOfSrc);
    <T extends Serializable> WriterFunction write(Publisher<T> obj, Class<T> typeOfSrc);

}
