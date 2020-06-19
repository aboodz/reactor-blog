package io.github.aboodz.summer.server.serdes;

import com.google.gson.Gson;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class GsonResultWriter implements ResultWriter {

    private final Gson gson;

    @Inject
    GsonResultWriter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Supplier<String> write(Object obj) {
        return () -> gson.toJson(obj);
    }

    @Override
    public Supplier<String> write(Object obj, Type typeOfSrc) {
        return () -> gson.toJson(obj, typeOfSrc);
    }

}
