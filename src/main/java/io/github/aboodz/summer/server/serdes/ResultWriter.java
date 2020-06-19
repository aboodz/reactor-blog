package io.github.aboodz.summer.server.serdes;

import java.lang.reflect.Type;

public interface ResultWriter {

    WriterFunction write(Object obj);
    WriterFunction write(Object obj, Type typeOfSrc);

}
