package io.github.aboodz.summer.blog.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Blog implements Serializable {
    private final String text;
}
