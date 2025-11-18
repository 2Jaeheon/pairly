package pairly.domain;

import java.util.Objects;

public class Marker {
    private static final String COMMENT_IS_NOT_NULL = "코멘트는 null일 수 없습니다.";

    private final String comment;

    public Marker(String comment) {
        this.comment = Objects.requireNonNull(comment, COMMENT_IS_NOT_NULL);
    }

    public String getComment() {
        return comment;
    }
}
