package io.github.aboodz.summer.blog;

import com.google.inject.AbstractModule;
import io.github.aboodz.summer.blog.api.BlogApiModule;
import io.github.aboodz.summer.db.DatabaseModule;
import io.github.aboodz.summer.server.ServerModule;

public class BlogApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ServerModule());
        install(new DatabaseModule());
        install(new BlogApiModule());
    }

}
