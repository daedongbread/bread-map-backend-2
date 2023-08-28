package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "LIKES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedLike extends BaseEntity {

    private final int MAXIMUM_FEED_LIKE_COUNT = 5;
    private final int UNLIKE_STATUS_COUNT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private CurationFeed curationFeed;

    private int count;

    public FeedLike(User user, CurationFeed curationFeed) {
        this.user = user;
        this.curationFeed = curationFeed;
    }

    public FeedLike(Long id, User user, CurationFeed curationFeed, int count) {
        this.id = id;
        this.user = user;
        this.curationFeed = curationFeed;
        this.count = count;
    }

    public void increase() {
        this.count++;
    }

    public void decrease() {
        this.count--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedLike feedLike = (FeedLike) o;
        return Objects.equals(user, feedLike.user)
                && Objects.equals(curationFeed, feedLike.curationFeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, curationFeed);
    }
}
