package io.github.aboodz.summer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.aboodz.summer.blog.BlogModule;
import io.github.aboodz.summer.server.HttpServer;
import io.github.aboodz.summer.server.ServerModule;

public class SummerApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ServerModule(), new BlogModule());

        HttpServer server = injector.getInstance(HttpServer.class);

        server.start();
    }

}
