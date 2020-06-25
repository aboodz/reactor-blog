package io.github.aboodz.summer.blog;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.github.aboodz.summer.blog.api.BlogApiModule;
import io.github.aboodz.summer.blog.api.BlogHandler;
import io.github.aboodz.summer.blog.api.BlogRoutes;
import io.github.aboodz.summer.db.DatabaseModule;
import io.github.aboodz.summer.server.HandlerResolver;
import io.github.aboodz.summer.server.Routable;
import io.github.aboodz.summer.server.ServerModule;

public class BlogApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ServerModule());
        install(new DatabaseModule());
        install(new BlogApiModule());
    }


}
