package io.github.aboodz.summer.blog.domain;

import lombok.Data;
import lombok.With;

import java.io.Serializable;
import java.util.Set;

@Data
@With
public class Post implements Serializable {
    private final Long id;
    private final String title;
    private final String body;
    private final Set<String> keywords;
}
