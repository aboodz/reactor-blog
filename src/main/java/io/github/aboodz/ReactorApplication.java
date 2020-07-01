package io.github.aboodz;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.aboodz.blog.BlogApplicationModule;
import io.github.aboodz.server.HttpServer;

public class ReactorApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BlogApplicationModule());

        HttpServer server = injector.getInstance(HttpServer.class);

        server.start();
    }

}
