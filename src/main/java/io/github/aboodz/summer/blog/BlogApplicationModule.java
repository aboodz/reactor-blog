package io.github.aboodz.summer.blog;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.github.aboodz.summer.server.HandlerResolver;
import io.github.aboodz.summer.server.Routable;
import io.github.aboodz.summer.server.ServerModule;

public class BlogApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ServerModule());
    }

    @ProvidesIntoSet
    public Routable blogRoutes(BlogHandler blogHandler, HandlerResolver resolver) {
        return new BlogRoutes(blogHandler, resolver);
    }

}
