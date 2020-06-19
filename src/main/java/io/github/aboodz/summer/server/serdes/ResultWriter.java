package io.github.aboodz.summer.server.serdes;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ResultWriter {

    Supplier<String> write(Object obj);
    Supplier<String> write(Object obj, Type typeOfSrc);

}
