package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Embeddable
public class FeedLikes {

    @OneToMany(
            mappedBy = "curationFeed",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<FeedLike> likes;

    public FeedLikes() {
        this(new ArrayList<>());
    }
    public FeedLikes(List<FeedLike> likes) {
        this.likes = likes;
    }

    public int getCounts() {
        return likes.stream()
                .mapToInt(FeedLike::getCount)
                .sum();
    }

    public int getCountsByUser(Long userId) {
        return likes.stream()
                .filter(like -> Objects.equals(like.getUser().getId(), userId))
                .mapToInt(FeedLike::getCount)
                .sum();
    }

    public boolean contains(FeedLike like) {
        return likes.stream()
                .anyMatch(like::equals);
    }

    public Optional<FeedLike> find(FeedLike like) {
        return likes.stream()
                .filter(feedLike -> feedLike.equals(like))
                .findAny();
    }

    public FeedLike add(FeedLike feedLike) {

        likes.add(feedLike);

        return feedLike;
    }

    public List<FeedLike> getFeedLikes() {
        return this.likes;
    }

    private boolean validateLike(FeedLike like) {
        return like.getCount() >= 5;
    }

    private boolean validateUnlike(FeedLike like) {
        return like.getCount() <= 0;
    }

    public void like(FeedLike like) {

        if (validateLike(like)) {
            throw new DaedongException(DaedongStatus.CANNOT_LIKE_MORE_THAN_COUNT);
        }

        like.increase();
    }

    public void unLike(FeedLike like) {

        if (validateUnlike(like)) {
            throw new DaedongException(DaedongStatus.CANNOT_UNLIKE_UNDER_ZERO);
        }

        like.decrease();
    }
}
