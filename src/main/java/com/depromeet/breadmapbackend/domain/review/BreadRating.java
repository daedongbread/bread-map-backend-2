package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BreadRating extends BaseEntity{
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bread_id")
    private Bread bread;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private BreadReview breadReview;

    @Column(nullable = false)
    private Long rating;

    @Column(nullable = false, columnDefinition = "boolean default 1")
    private boolean isUse;

    @Builder
    private BreadRating(User user, Bakery bakery, Bread bread, BreadReview breadReview, Long rating, boolean isUse) {
        this.user = user;
        this.bakery = bakery;
        this.bread = bread;
        this.breadReview = breadReview;
        this.rating = rating;
        this.isUse = isUse;
    }
}
