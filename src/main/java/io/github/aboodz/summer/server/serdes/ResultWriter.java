package io.github.aboodz.summer.server.serdes;

import org.reactivestreams.Publisher;

import java.io.Serializable;
import java.lang.reflect.Type;

public interface ResultWriter {

//    WriterFunction write(Object obj);
//    WriterFunction write(Object obj, Type typeOfSrc);
    WriterFunction write(Publisher<? extends Serializable> obj, Type typeOfSrc);

}
