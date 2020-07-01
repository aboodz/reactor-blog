package io.github.aboodz.blog;

import com.google.inject.AbstractModule;
import io.github.aboodz.blog.api.BlogApiModule;
import io.github.aboodz.db.DatabaseModule;
import io.github.aboodz.server.ServerModule;

public class BlogApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ServerModule());
        install(new DatabaseModule());
        install(new BlogApiModule());
    }

}
