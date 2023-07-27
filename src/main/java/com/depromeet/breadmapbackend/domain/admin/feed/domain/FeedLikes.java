package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class FeedLikes {

    private final int MAXIMUM_FEED_LIKE_COUNT = 5;
    private final int UNLIKE_STATUS_COUNT = 0;

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

    public FeedLike find(FeedLike like) {
        return likes.stream()
                .filter(e -> Objects.equals(e.getId(), like.getId()))
                .findAny()
                .orElseThrow(() -> new DaedongException(DaedongStatus.CANNOT_UNLIKE_UNDER_ZERO));
    }

    private boolean validateLike(FeedLike like) {
        return like.getCount() >= MAXIMUM_FEED_LIKE_COUNT;
    }

    private boolean validateUnlike(FeedLike like) {
        return like.getCount() <= UNLIKE_STATUS_COUNT;
    }

    public void like(FeedLike like) {
        if (validateLike(like)) {
            throw new DaedongException(DaedongStatus.CANNOT_LIKE_MORE_THAN_COUNT);
        }

        like.increase();
    }

    public void unLike(FeedLike like) {
        if (!this.contains(like) || validateUnlike(like)) {
            throw new DaedongException(DaedongStatus.CANNOT_UNLIKE_UNDER_ZERO);
        }

        like.decrease();
    }
}
