package io.github.aboodz.blog.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@With
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
public class Post implements Serializable {
    private final Long id;
    private @NonNull String title;
    private @NonNull String body;
    private @NonNull Set<String> keywords = new HashSet<>();
}
