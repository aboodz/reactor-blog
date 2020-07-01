package io.github.aboodz.blog.api;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.github.aboodz.server.Routable;

public class BlogApiModule extends AbstractModule {

    @ProvidesIntoSet
    public Routable blogRoutes(BlogHandler blogHandler) {
        return new BlogRoutes(blogHandler);
    }


}
