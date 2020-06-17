package io.github.aboodz.summer.blog;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.github.aboodz.summer.server.Routable;

public class BlogModule extends AbstractModule {

    @ProvidesIntoSet
    public Routable blogRoutes(BlogHandler blogHandler) {
        return new BlogRoutes(blogHandler);
    }

}
