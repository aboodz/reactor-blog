package io.github.aboodz.server.serdes.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Provider;

public class GsonSerdesProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .create();
    }
}
