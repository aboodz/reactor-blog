package io.github.aboodz.summer.blog.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class Post implements Serializable {
    private final Long id;
    private final String body;
    private final String title;
    private final Set<String> keywords;
}
